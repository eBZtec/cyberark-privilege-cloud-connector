package br.tec.ebz.connid.connector.cyberark;

import br.tec.ebz.connid.connector.cyberark.operations.UserOperations;
import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import br.tec.ebz.connid.connector.cyberark.utils.BasicTestConnection;
import br.tec.ebz.connid.connector.cyberark.utils.GetUser;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

public class CreateOpTest {

    private ConnectorFacade connectorFacade;
    private GetUser userTest;

    @BeforeTest
    public void init() {
        BasicTestConnection basicTestConnection = new BasicTestConnection();
        connectorFacade = basicTestConnection.getTestConnection();
        userTest = new GetUser();
    }

    @Test
    public void shouldCreateActiveUser() {
        String userName = userTest.getUserName();

        Set<Attribute> creationAttributes = new HashSet<>();
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.USERNAME.getAttribute(), userName));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.ENABLE_USER.getAttribute(), true));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.CHANGE_PASS_ON_NEXT_LOGON.getAttribute(), false));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.SUSPENDED.getAttribute(), false));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.PASSWORD_NEVER_EXPIRES.getAttribute(), true));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.DESCRIPTION.getAttribute(), "User created by CyberArk Connector"));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.INITIAL_PASSWORD.getAttribute(), "Smartwa123"));
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.LOCATION.getAttribute(), "\\"));

        List<String> vaultAuthorizations = new ArrayList<>();
        vaultAuthorizations.add("AddSafes");
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.VAULT_AUTHORIZATION.getAttribute(), vaultAuthorizations));

        List<String> unauthorized = new ArrayList<>();
        unauthorized.add("PSMP");
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.UNAUTHORIZED_INTERFACES.getAttribute(), unauthorized));

        List<String> authMethods = new ArrayList<>();
        authMethods.add("AuthTypePass");
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.AUTHENTICATION_METHOD.getAttribute(), authMethods));

        // Business Address
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute() + "." + UserSchemaAttributes.WORK_STREET.getAttribute(),
                "9999999"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute() + "." + UserSchemaAttributes.WORK_CITY.getAttribute(),
                "Petah Tikva"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute() + "." + UserSchemaAttributes.WORK_STATE.getAttribute(),
                "Petah Tikva"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute() + "." + UserSchemaAttributes.WORK_ZIP.getAttribute(),
                "99999-999"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.BUSINESS_ADDRESS.getAttribute() + "." + UserSchemaAttributes.WORK_COUNTRY.getAttribute(),
                "Israel"
        ));
        // Internet
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.HOME_PAGE.getAttribute(),
                "cyberark.com"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.HOME_EMAIL.getAttribute(),
                "user@gmail.com"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.BUSINESS_EMAIL.getAttribute(),
                "user@cyberark.com"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.OTHER_EMAIL.getAttribute(),
                "user2@gmail.com"
        ));
        // Phones
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PHONES.getAttribute() + "." + UserSchemaAttributes.HOME_NUMBER.getAttribute(),
                "555123456"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PHONES.getAttribute() + "." + UserSchemaAttributes.BUSINESS_NUMBER.getAttribute(),
                "555145678"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PHONES.getAttribute() + "." + UserSchemaAttributes.CELLULAR_NUMBER.getAttribute(),
                "555109876"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PHONES.getAttribute() + "." + UserSchemaAttributes.PAGER_NUMBER.getAttribute(),
                "111111"
        ));
        //Personal details
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.STREET.getAttribute(),
                "Dizzengof 56"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.CITY.getAttribute(),
                "Tel Aviv"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.STATE.getAttribute(),
                "Israel"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.ZIP.getAttribute(),
                "123456"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.COUNTRY.getAttribute(),
                "Israel"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.TITLE.getAttribute(),
                "Mr. VIP"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.ORGANIZATION.getAttribute(),
                "CyberArk"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.DEPARTMENT.getAttribute(),
                "R&D"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.PROFESSION.getAttribute(),
                "Software development"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.FIRST_NAME.getAttribute(),
                "John"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.MIDDLE_NAME.getAttribute(),
                "Doe"
        ));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.PERSONAL_DETAILS.getAttribute() + "." + UserSchemaAttributes.LAST_NAME.getAttribute(),
                "Smith"
        ));

        Uid uid = connectorFacade.create(UserOperations.OBJECT_CLASS, creationAttributes, null);

        AssertJUnit.assertNotNull(uid);

        BasicTestConnection basicTestConnection2 = new BasicTestConnection();
        ConnectorFacade connectorFacade2 = basicTestConnection2.getTestConnection();

        connectorFacade2.delete(UserOperations.OBJECT_CLASS, uid, null);
    }
}
