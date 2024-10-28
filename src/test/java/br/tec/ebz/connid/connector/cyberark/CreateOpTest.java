package br.tec.ebz.connid.connector.cyberark;

import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import br.tec.ebz.connid.connector.cyberark.utils.BasicTestConnection;
import br.tec.ebz.connid.connector.cyberark.utils.GetUser;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    public void shouldCreateUser() {
        String userName = userTest.getUserName();

        Set<Attribute> creationAttributes = new HashSet<>();
        creationAttributes.add(AttributeBuilder.build(UserSchemaAttributes.USERNAME.getAttribute(), userName));
        creationAttributes.add(AttributeBuilder.build(
                UserSchemaAttributes.INTERNET.getAttribute() + "." + UserSchemaAttributes.HOME_NUMBER
                , "0000-0000"
        ));

        connectorFacade.create(ObjectClass.ACCOUNT, creationAttributes, null);
    }
}
