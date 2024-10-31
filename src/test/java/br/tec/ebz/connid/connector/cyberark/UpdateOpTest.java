package br.tec.ebz.connid.connector.cyberark;

import br.tec.ebz.connid.connector.cyberark.operations.UserOperations;
import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import br.tec.ebz.connid.connector.cyberark.utils.BasicTestConnection;
import br.tec.ebz.connid.connector.cyberark.utils.GetUser;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateOpTest {

    private ConnectorFacade connectorFacade;
    private GetUser userTest;

    @BeforeTest
    public void init() {
        BasicTestConnection basicTestConnection = new BasicTestConnection();
        connectorFacade = basicTestConnection.getTestConnection();
        userTest = new GetUser();
    }

    @Test
    public void shouldUpdateUser() {
        Set<AttributeDelta> attributes = new HashSet<>();

        AttributeDeltaBuilder attr1 = new AttributeDeltaBuilder();
        attr1.setName(UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.HOME_EMAIL.getAttribute());
        attr1.addValueToReplace("newemail2@gmail.com");

        AttributeDeltaBuilder attr2 = new AttributeDeltaBuilder();
        attr2.setName(UserSchemaAttributes.ENABLE_USER.getAttribute());
        attr2.addValueToReplace(true);

        AttributeDeltaBuilder attr3 = new AttributeDeltaBuilder();
        attr3.setName(UserSchemaAttributes.UNAUTHORIZED_INTERFACES.getAttribute());
        List<String> unauthorizedInterfaces = new ArrayList<>();
        unauthorizedInterfaces.add("PSMP");
        attr3.addValueToRemove(unauthorizedInterfaces);

        attributes.add(attr1.build());
        attributes.add(attr2.build());
        attributes.add(attr3.build());

        Set<AttributeDelta> attributeDeltas = connectorFacade.updateDelta(UserOperations.OBJECT_CLASS, new Uid("44"), attributes, null);
    }

    @Test
    public void createNewUserAndResetHisPassword() {
        String userName = userTest.getUserName();

        Set<Attribute> creationAttributes = new HashSet<>();
        creationAttributes.add(AttributeBuilder.build(Name.NAME, userName));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.ENABLE_USER.getAttribute(), true));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.CHANGE_PASS_ON_NEXT_LOGON.getAttribute(), false));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.DESCRIPTION.getAttribute(), "User created by CyberArk Connector"));
        creationAttributes.add(AttributeBuilder.build(OperationalAttributes.PASSWORD_NAME, new GuardedString("Smartway123".toCharArray())));

        Uid uid = connectorFacade.create(UserOperations.OBJECT_CLASS, creationAttributes, null);

        AssertJUnit.assertNotNull(uid);

        Set<AttributeDelta> attributes = new HashSet<>();

        AttributeDeltaBuilder attr1 = new AttributeDeltaBuilder();
        attr1.setName(OperationalAttributes.PASSWORD_NAME);
        attr1.addValueToReplace(new GuardedString("Smartway1234!".toCharArray()));

        attributes.add(attr1.build());

        Set<AttributeDelta> attributeDeltas = connectorFacade.updateDelta(UserOperations.OBJECT_CLASS, new Uid(uid.getUidValue()), attributes, null);
    }
}
