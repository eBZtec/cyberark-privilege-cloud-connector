package br.tec.ebz.connid.connector.cyberark;

import br.tec.ebz.connid.connector.cyberark.operations.UserOperations;
import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import br.tec.ebz.connid.connector.cyberark.utils.GetUser;
import kong.unirest.core.json.JSONObject;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

public class UserOperationsTest {

    private static Log LOG = Log.getLog(UserOperationsTest.class);

    private UserOperations userOperations;
    private GetUser user;

    @BeforeTest
    public void init() {
        userOperations = new UserOperations(null);
        user = new GetUser();

    }

    @Test
    public void testBuildJson() {
        String userName = user.getUserName();

        Set<Attribute> creationAttributes = new HashSet<>();
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.USERNAME.getAttribute(), userName));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.HOME_NUMBER.getAttribute()
                , "0000-0000"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.HOME_EMAIL.getAttribute()
                , "test@example.com"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.ENABLE_USER.getAttribute()
                , true
        ));

        JSONObject json = userOperations.buildJson(creationAttributes);

        AssertJUnit.assertEquals(userName, json.getString(UserSchemaAttributes.USERNAME.getAttribute()));
        AssertJUnit.assertTrue(json.has(UserSchemaAttributes.INTERNET.getAttribute()));
        JSONObject internet = json.getJSONObject(UserSchemaAttributes.INTERNET.getAttribute());
        AssertJUnit.assertNotNull(internet);
        AssertJUnit.assertEquals("0000-0000", internet.getString(UserSchemaAttributes.HOME_NUMBER.getAttribute()));
        AssertJUnit.assertEquals(user.homeEmail(), internet.getString(UserSchemaAttributes.HOME_EMAIL.getAttribute()));
        AssertJUnit.assertTrue(json.getBoolean(UserSchemaAttributes.ENABLE_USER.getAttribute()));

    }

    @Test
    public void testTranslateToConnectorObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user.getId());
        jsonObject.put("username", user.getUserName());
        jsonObject.put("enableUser", true);
        jsonObject.put("groupsMembership", user.getGroups());
        jsonObject.put("lastSuccessfullLoginDate", user.getLastSuccessfullLoginDate());

        // Business Address
        Map<String, String> businessAddress = new HashMap<>();
        businessAddress.put("workStreet", user.getWorkStreet());
        businessAddress.put("workCity", user.getWorkCity());
        businessAddress.put("workState", user.getWorkState());
        businessAddress.put("workZip", user.getWorkZip());
        businessAddress.put("workCountry", user.getWorkCountry());

        jsonObject.put("businessAddress", businessAddress);

        ConnectorObject connectorObject = userOperations.translateToConnectorObject(jsonObject);

        AssertJUnit.assertEquals(connectorObject.getObjectClass(), ObjectClass.ACCOUNT);

        String id = AttributeUtil.getStringValue(connectorObject.getAttributeByName(Uid.NAME));
        String name = AttributeUtil.getStringValue(connectorObject.getAttributeByName(Name.NAME));
        String username = AttributeUtil.getStringValue(connectorObject.getAttributeByName("username"));
        Boolean enableUser = AttributeUtil.getBooleanValue(connectorObject.getAttributeByName("enableUser"));
        List<Object> groups = connectorObject.getAttributeByName("groupsMembership").getValue();
        long lastSuccessfullLoginDate = AttributeUtil.getLongValue(connectorObject.getAttributeByName("lastSuccessfullLoginDate"));

        String workStreet = AttributeUtil.getStringValue(connectorObject.getAttributeByName("businessAddress.workStreet"));

        AssertJUnit.assertEquals(user.getId(), Integer.parseInt(id));
        AssertJUnit.assertEquals(user.getUserName(), name);
        AssertJUnit.assertEquals(user.getUserName(), username);
        AssertJUnit.assertTrue(enableUser);
        AssertJUnit.assertEquals(3, groups.size());
        AssertJUnit.assertEquals(user.getLastSuccessfullLoginDate(), lastSuccessfullLoginDate);

        AssertJUnit.assertEquals(user.getWorkStreet(), workStreet);
    }
}
