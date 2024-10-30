package br.tec.ebz.connid.connector.cyberark.operations;

import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import com.evolveum.polygon.common.GuardedStringAccessor;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;

import java.util.*;

public class ObjectOperations {

    private static final Log LOG = Log.getLog(ObjectOperations.class);

    public JSONObject buildJson(Set<Attribute> attributes) {

        JSONObject json = new JSONObject();

        for (Attribute attribute: attributes) {
            String attributeName = attribute.getName();
            String attributeNameNative = attribute.getName();

            if (attributeName.equals(OperationalAttributes.PASSWORD_NAME)) {
                attributeName = UserSchemaAttributes.INITIAL_PASSWORD.getAttribute();
            }

            if (attributeName.equals(Name.NAME)) {
                attributeName = UserSchemaAttributes.USERNAME.getAttribute();
            }

            String[] attributePath = attributeName.split("\\.");

            JSONObject current = json;
            for (int i = 0; i < attributePath.length - 1; i++) {
                if (!current.has(attributePath[i])) {
                    JSONObject child = new JSONObject();
                    current.put(attributePath[i], child);
                    current = child;
                } else {
                    current = current.getJSONObject(attributePath[i]);
                }
            }

            String key = attributePath[attributePath.length - 1];

            UserSchemaAttributes userSchemaAttribute = UserSchemaAttributes.findAttribute(key);

            if (userSchemaAttribute == null)  continue;

            if (!attributeName.equals(UserSchemaAttributes.INITIAL_PASSWORD.getAttribute())) {
                current.put(key, getAttributeValue(attributeNameNative, userSchemaAttribute.getType(), attributes));
            } else {
                String value = getGuardedStringValue(OperationalAttributes.PASSWORD_NAME, attributes);
                current.put(key, value);
            }
        }

        return json;
    }

    protected String getGuardedStringValue(String name, Set<Attribute> attributes) {
        LOG.ok("Processing guarded string attribute {0}", name);

        Attribute attr = AttributeUtil.find(name, attributes);

        if (attr == null) {
            return null;
        }

        GuardedString guardedString = AttributeUtil.getGuardedStringValue(attr);
        GuardedStringAccessor accessor = new GuardedStringAccessor();

        guardedString.access(accessor);

        return accessor.getClearString();
    }


    protected static <T> T getAttributeValue(String name, Class<T> type, Set<Attribute> attributes) {
        LOG.ok("Processing attribute {0} of the type {1}", name, type.toString());

        Attribute attr = AttributeUtil.find(name, attributes);

        if (attr == null) {
            return null;
        }

        if (String.class.equals(type)) {
            return (T) AttributeUtil.getStringValue(attr);
        } else if (Long.class.equals(type)) {
            return (T) AttributeUtil.getLongValue(attr);
        } else if (Integer.class.equals(type)) {
            return (T) AttributeUtil.getIntegerValue(attr);
        } else if (GuardedString.class.equals(type)) {
            return (T) AttributeUtil.getGuardedStringValue(attr);
        } else if (Boolean.class.equals(type)) {
            return (T) AttributeUtil.getBooleanValue(attr);
        } else if (List.class.equals(type)) {
            return (T) attr.getValue();
        } else if(Date.class.equals(type)) {
            return (T) AttributeUtil.getDateValue(attr);
        } else {
            throw new InvalidAttributeValueException("Unknown value type " + type);
        }
    }

    protected <T> T getJsonAttributeValue(JSONObject object, Class<T> type, String attributeName) {
        Object attributeValue = object.has(attributeName) ? object.get(attributeName) : null;

        if (attributeValue == null || JSONObject.NULL.equals(attributeValue)) {
            return null;
        }

        if (attributeValue instanceof JSONArray) {
            List<String> values = new ArrayList<>();
            JSONArray objectArray = object.getJSONArray(attributeName);

            for (int i = 0; i < objectArray.length(); i++) {
                if (objectArray.get(i) instanceof JSONObject) {
                    JSONObject jsonObject = objectArray.getJSONObject(i);
                    values.add(jsonObject.toString());
                } else {
                    values.add(String.valueOf(objectArray.get(i)));
                }
            }

            return (T) values;
        }

        return (T) attributeValue;

    }

    protected void validate(Set<Attribute> attributes, Set<String> requiredAttributes) {
        for (String requiredAttributeName: requiredAttributes) {
            Attribute attribute = AttributeUtil.find(requiredAttributeName, attributes);
            String attributeValue = getAttributeValue(requiredAttributeName, String.class, attributes);

            if (attribute == null || attributeValue == null || attributeValue.isEmpty()) {
                LOG.error("Attribute {0} cannot be empty or null", requiredAttributeName);
                throw new InvalidAttributeValueException("Attribute " + requiredAttributeName + " is required, cannot be empty or null.");
            }

        }
    }

    protected AttributeInfo buildAttributeInfo(String name, Class<?> type, String nativeName, AttributeInfo.Flags... flags) {

        AttributeInfoBuilder aib = new AttributeInfoBuilder(name);
        aib.setType(type);

        if (nativeName == null) {
            nativeName = name;
        }

        aib.setNativeName(nativeName);

        if (flags.length != 0) {
            Set<AttributeInfo.Flags> set = new HashSet<>();
            set.addAll(Arrays.asList(flags));
            aib.setFlags(set);
        }

        return aib.build();
    }

    protected void addAttribute(ConnectorObjectBuilder builder, String attrName, Object value) {
        LOG.info("Processing attribute {0} with value(s) {1}", attrName, value);

        if (value == null) {
            return;
        }

        AttributeBuilder attributeBuilder = new AttributeBuilder();
        attributeBuilder.setName(attrName);

        if (value instanceof Collection) {
            attributeBuilder.addValue((Collection<?>) value);
        } else {
            attributeBuilder.addValue(value);
        }

        builder.addAttribute(attributeBuilder.build());
    }

    protected void getUidIfExists(JSONObject object, String attrName, Class<?> type, ConnectorObjectBuilder connectorObjectBuilder) {
        Object value = getJsonAttributeValue(object, type, attrName);

        if (value == null) {
            throw new UnknownUidException("Could not define UID from attribute " + attrName);
        }

        addAttribute(connectorObjectBuilder, Uid.NAME, String.valueOf(value));
    }

    protected void getNameIfExists(JSONObject object, String attrName, Class<?> type, ConnectorObjectBuilder connectorObjectBuilder) {
        Object value = getJsonAttributeValue(object, type, attrName);

        if (value == null) {
            throw new InvalidAttributeValueException("Could not define NAME from attribute " + attrName);
        }

        addAttribute(connectorObjectBuilder, Name.NAME, value);
    }

    protected void getIfExists(JSONObject object, String attributeName, Class<?> type, ConnectorObjectBuilder connectorObjectBuilder) {
        Object value = getJsonAttributeValue(object, type, attributeName);

        if (value != null) {
            addAttribute(connectorObjectBuilder, attributeName, value);
        }
    }

    protected void getIfExists(JSONObject object, String attributeName, String subAttributeName, Class<?> type, ConnectorObjectBuilder connectorObjectBuilder) {
        if (object.has(attributeName)) {
            JSONObject item = object.getJSONObject(attributeName);

            if (item.has(subAttributeName)) {
                Object value = getJsonAttributeValue(item, type, subAttributeName);

                if (value != null) {
                    addAttribute(connectorObjectBuilder, attributeName + "." + subAttributeName, value);
                }
            }
        }
    }
}
