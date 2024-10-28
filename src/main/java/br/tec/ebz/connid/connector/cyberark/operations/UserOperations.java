package br.tec.ebz.connid.connector.cyberark.operations;

import br.tec.ebz.connid.connector.cyberark.CyberArkEndpoint;
import br.tec.ebz.connid.connector.cyberark.interfaces.IUserOperations;
import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserOperations extends ObjectOperations implements IUserOperations {

    private static final Log LOG = Log.getLog(UserOperations.class);

    public static final String OBJECT_CLASS_NAME = "user";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    private static final String CREATE_USER_ENDPOINT = "/PasswordVault/API/Users/";
    private static final String ACTIVATE_USER_ENDPOINT = "/PasswordVault/API/Users/%s/Activate/";
    private static final String ENABLE_USER_ENDPOINT = "/PasswordVault/API/Users/%s/enable/";
    private static final String DISABLE_USER_ENDPOINT = "/PasswordVault/API/Users/%s/disable/";
    private static final String DELETE_USER_ENDPOINT = "/PasswordVault/API/Users/%s/";
    private static final String SEARCH_USERS_ENDPOINT = "/PasswordVault/API/Users/";
    private static final String GET_USER_DETAILS = "/PasswordVault/API/Users/%s/";

    private final CyberArkEndpoint cyberArkEndpoint;

    public UserOperations(CyberArkEndpoint cyberArkEndpoint) {
        this.cyberArkEndpoint = cyberArkEndpoint;
    }

    @Override
    public String type() {
        return OBJECT_CLASS_NAME;
    }

    @Override
    public Uid create(Set<Attribute> attributeSet) {

        JSONObject body = buildJson(attributeSet);
        LOG.info("JSON {0}", body.toString());
        HttpResponse<JsonNode> response = cyberArkEndpoint.post(CREATE_USER_ENDPOINT, body);

        if (LOG.isOk()) {
            LOG.ok("User created successfully");
        }

        LOG.info("Response for user creation {0}", response.getBody().toString());

        String uid = getId(response.getBody().getObject());

        if (uid == null) {
            throw new UnknownUidException("Could not determine UID from response " + response.getBody());
        }

        return new Uid(uid);
    }

    public String getId(JSONObject jsonObject) {
        if (!jsonObject.has(UserSchemaAttributes.ID.getAttribute())) return null;

        return String.valueOf(jsonObject.getInt(UserSchemaAttributes.ID.getAttribute()));
    }

    @Override
    public void changePassword(String uid, GuardedString password) {

    }

    @Override
    public void enableUser(String uid) {

    }

    @Override
    public void disableUser(String uid) {

    }

    @Override
    public void activateUser(String uid) {

    }


    @Override
    public String update(Uid uid, Set<AttributeDelta> deltas) {
        return "";
    }

    @Override
    public JSONObject getUserDetails(String id) {
        String endpoint = String.format(GET_USER_DETAILS, id);

        HttpResponse<JsonNode> response = cyberArkEndpoint.get(endpoint);

        return response.getBody().getObject();
    }

    @Override
    public void delete(Uid uid) {
        String endpoint = String.format(DELETE_USER_ENDPOINT, uid.getUidValue());

        cyberArkEndpoint.delete(endpoint);

        if (LOG.isOk()) {
            LOG.ok("User {0} deleted successfully", uid.getUidValue());
        }
    }

    @Override
    public void search(Filter filter, ResultsHandler resultsHandler, OperationOptions options) {
        if (filter != null) {
            searchByFilter(filter, resultsHandler, options);
        } else {
            searchAll(resultsHandler, options);
        }
    }

    @Override
    public void searchAll(ResultsHandler resultsHandler, OperationOptions options) {;
        List<JSONObject> users = cyberArkEndpoint.get(SEARCH_USERS_ENDPOINT, null, options);

        for(JSONObject user: users) {
            String id = user.getString(UserSchemaAttributes.ID.getAttribute());
            String userName = user.getString(UserSchemaAttributes.USERNAME.getAttribute());
            LOG.info("UserName {0}", userName);

            // Ignore Master user, for some reason it give status code 500
            if (id.equals("0")) continue;

            LOG.info("Searching by user {0}", id);

            JSONObject userDetailed = getUserDetails(id);

            resultsHandler.handle(translateToConnectorObject(userDetailed));
        }
    }

    @Override
    public void searchByFilter(Filter filter, ResultsHandler resultsHandler, OperationOptions options) {

        if (filter instanceof EqualsFilter equalsFilter) {
            LOG.info("Searching user by Equals filter");
            Attribute filterAttribute = equalsFilter.getAttribute();

            if (filterAttribute == null) {
                throw new InvalidAttributeValueException("Could not search by Equals Filter, reason: Attribute to search not defined");
            }

            String attrName = filterAttribute.getName();
            List<Object> filterValues = filterAttribute.getValue();

            if (filterValues.isEmpty()) {
                throw new InvalidAttributeValueException("Could not search by Equals Filter, reason: No value defined");
            } else if (filterValues.size() > 1) {
                throw new UnsupportedOperationException("Could not search by Equals Filter, reason: More than one value defined");
            }

            LOG.info("Processing equals filter search for attribute {0} values {1}", attrName, filterValues);


            if (attrName.equals(Uid.NAME)) {
                LOG.info("Searching user by UID value");

                JSONObject user = getUserDetails((String) filterValues.get(0));

                if (user != null) {
                    ConnectorObject connectorObject = translateToConnectorObject(user);
                    resultsHandler.handle(connectorObject);
                }

            } else if(attrName.equals(Name.NAME)
                    || attrName.equals(UserSchemaAttributes.USERNAME.getAttribute())
                    || attrName.equals(UserSchemaAttributes.COMPONENT_USER.getAttribute())
                    || attrName.equals(UserSchemaAttributes.USER_TYPE.getAttribute())
            ) {
                if (attrName.equals(Name.NAME)) {
                    attrName = UserSchemaAttributes.USERNAME.getAttribute();
                }

                LOG.info("Searching users by attribute {0} and value {1}", attrName, filterValues.get(0));

                Map<String, Object> query = new HashMap<>();
                query.put(attrName, filterValues.get(0));

                List<JSONObject> users = cyberArkEndpoint.get(SEARCH_USERS_ENDPOINT, query, options);

                for(JSONObject user: users) {
                    String id = user.getString(UserSchemaAttributes.ID.getAttribute());
                    JSONObject userDetailed = getUserDetails(id);

                    resultsHandler.handle(translateToConnectorObject(userDetailed));
                }
            } else {
                throw new UnsupportedOperationException("Could not search user, reason: Attribute " + attrName + " not supported");
            }
        } else {
            throw new UnsupportedOperationException("Could not search user, reason: Filter " + filter.getClass().getName() + " not supported");
        }

    }

    @Override
    public ConnectorObject translateToConnectorObject(JSONObject user) {
        ConnectorObjectBuilder connectorObjectBuilder = new ConnectorObjectBuilder();
        connectorObjectBuilder.setObjectClass(ObjectClass.ACCOUNT);

        getNameIfExists(user, UserSchemaAttributes.USERNAME.getAttribute(), UserSchemaAttributes.USERNAME.getType(), connectorObjectBuilder);
        getUidIfExists(user, UserSchemaAttributes.ID.getAttribute(), UserSchemaAttributes.ID.getType(), connectorObjectBuilder);

        getIfExists(user, UserSchemaAttributes.USERNAME.getAttribute(), UserSchemaAttributes.USERNAME.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.SOURCE.getAttribute(), UserSchemaAttributes.SOURCE.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.USER_TYPE.getAttribute(), UserSchemaAttributes.USER_TYPE.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.GROUPS_MEMBERSHIP.getAttribute(), UserSchemaAttributes.GROUPS_MEMBERSHIP.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.VAULT_AUTHORIZATION.getAttribute(), UserSchemaAttributes.VAULT_AUTHORIZATION.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.LOCATION.getAttribute(), UserSchemaAttributes.LOCATION.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.COMPONENT_USER.getAttribute(), UserSchemaAttributes.COMPONENT_USER.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.ENABLE_USER.getAttribute(), UserSchemaAttributes.ENABLE_USER.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.CHANGE_PASS_ON_NEXT_LOGON.getAttribute(), UserSchemaAttributes.CHANGE_PASS_ON_NEXT_LOGON.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.EXPIRY_DATE.getAttribute(), UserSchemaAttributes.EXPIRY_DATE.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.SUSPENDED.getAttribute(), UserSchemaAttributes.SUSPENDED.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.LAST_SUCCESSFUL_LOGIN_DATE.getAttribute(), UserSchemaAttributes.LAST_SUCCESSFUL_LOGIN_DATE.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.UNAUTHORIZED_INTERFACES.getAttribute(), UserSchemaAttributes.UNAUTHORIZED_INTERFACES.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.PASSWORD_NEVER_EXPIRES.getAttribute(), UserSchemaAttributes.PASSWORD_NEVER_EXPIRES.getType(), connectorObjectBuilder);
        getIfExists(user, UserSchemaAttributes.DISTINGUISHED_NAME.getAttribute(), UserSchemaAttributes.DISTINGUISHED_NAME.getType(), connectorObjectBuilder);
        // Business Address
        getIfExists(
                user,
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute(),
                UserSchemaAttributes.WORK_STREET.getAttribute(),
                UserSchemaAttributes.WORK_STREET.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute(),
                UserSchemaAttributes.WORK_CITY.getAttribute(),
                UserSchemaAttributes.WORK_CITY.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute(),
                UserSchemaAttributes.WORK_STATE.getAttribute(),
                UserSchemaAttributes.WORK_STATE.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute(),
                UserSchemaAttributes.WORK_ZIP.getAttribute(),
                UserSchemaAttributes.WORK_ZIP.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute(),
                UserSchemaAttributes.WORK_COUNTRY.getAttribute(),
                UserSchemaAttributes.WORK_COUNTRY.getType(),
                connectorObjectBuilder
        );
        // Internet
        getIfExists(
                user,
                UserSchemaAttributes.INTERNET.getAttribute(),
                UserSchemaAttributes.HOME_PAGE.getAttribute(),
                UserSchemaAttributes.HOME_PAGE.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.INTERNET.getAttribute(),
                UserSchemaAttributes.HOME_EMAIL.getAttribute(),
                UserSchemaAttributes.HOME_EMAIL.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.INTERNET.getAttribute(),
                UserSchemaAttributes.BUSINESS_EMAIL.getAttribute(),
                UserSchemaAttributes.BUSINESS_EMAIL.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.INTERNET.getAttribute(),
                UserSchemaAttributes.OTHER_EMAIL.getAttribute(),
                UserSchemaAttributes.OTHER_EMAIL.getType(),
                connectorObjectBuilder
        );
        // Phones
        getIfExists(
                user,
                UserSchemaAttributes.PHONES.getAttribute(),
                UserSchemaAttributes.HOME_NUMBER.getAttribute(),
                UserSchemaAttributes.HOME_NUMBER.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PHONES.getAttribute(),
                UserSchemaAttributes.BUSINESS_NUMBER.getAttribute(),
                UserSchemaAttributes.BUSINESS_NUMBER.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PHONES.getAttribute(),
                UserSchemaAttributes.CELLULAR_NUMBER.getAttribute(),
                UserSchemaAttributes.CELLULAR_NUMBER.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PHONES.getAttribute(),
                UserSchemaAttributes.FAX_NUMBER.getAttribute(),
                UserSchemaAttributes.FAX_NUMBER.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PHONES.getAttribute(),
                UserSchemaAttributes.PAGER_NUMBER.getAttribute(),
                UserSchemaAttributes.PAGER_NUMBER.getType(),
                connectorObjectBuilder
        );
        // Personal Details
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.STREET.getAttribute(),
                UserSchemaAttributes.STREET.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.CITY.getAttribute(),
                UserSchemaAttributes.CITY.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.STATE.getAttribute(),
                UserSchemaAttributes.STATE.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.ZIP.getAttribute(),
                UserSchemaAttributes.ZIP.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.COUNTRY.getAttribute(),
                UserSchemaAttributes.COUNTRY.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.TITLE.getAttribute(),
                UserSchemaAttributes.TITLE.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.ORGANIZATION.getAttribute(),
                UserSchemaAttributes.ORGANIZATION.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.DEPARTMENT.getAttribute(),
                UserSchemaAttributes.DEPARTMENT.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.PROFESSION.getAttribute(),
                UserSchemaAttributes.PROFESSION.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.FIRST_NAME.getAttribute(),
                UserSchemaAttributes.FIRST_NAME.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.MIDDLE_NAME.getAttribute(),
                UserSchemaAttributes.MIDDLE_NAME.getType(),
                connectorObjectBuilder
        );
        getIfExists(
                user,
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute(),
                UserSchemaAttributes.LAST_NAME.getAttribute(),
                UserSchemaAttributes.LAST_NAME.getType(),
                connectorObjectBuilder
        );
        return connectorObjectBuilder.build();
    }

    @Override
    public ObjectClassInfo schema() {
        ObjectClassInfoBuilder objectClassInfoBuilder = new ObjectClassInfoBuilder();
        objectClassInfoBuilder.setType(type());

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Uid.NAME,
                        String.class,
                        UserSchemaAttributes.ID.getAttribute(),
                        AttributeInfo.Flags.REQUIRED
                )
        );
        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Name.NAME,
                        String.class,
                        UserSchemaAttributes.USERNAME.getAttribute(),
                        AttributeInfo.Flags.REQUIRED
                )
        );
        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        UserSchemaAttributes.SOURCE.getAttribute(),
                        UserSchemaAttributes.SOURCE.getType(),
                        null
                )
        );
        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        UserSchemaAttributes.USER_TYPE.getAttribute(),
                        UserSchemaAttributes.USER_TYPE.getType(),
                        null
                )
        );
        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        UserSchemaAttributes.GROUPS_MEMBERSHIP.getAttribute(),
                        UserSchemaAttributes.GROUPS_MEMBERSHIP.getType(),
                        null,
                        AttributeInfo.Flags.NOT_UPDATEABLE,
                        AttributeInfo.Flags.NOT_CREATABLE
                )
        );
        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        UserSchemaAttributes.VAULT_AUTHORIZATION.getAttribute(),
                        UserSchemaAttributes.VAULT_AUTHORIZATION.getType(),
                        null
                )
        );
        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        UserSchemaAttributes.LOCATION.getAttribute(),
                        UserSchemaAttributes.LOCATION.getType(),
                        null
                )
        );

        return objectClassInfoBuilder.build();
    }
}
