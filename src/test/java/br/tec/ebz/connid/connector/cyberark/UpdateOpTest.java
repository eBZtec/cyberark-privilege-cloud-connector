package br.tec.ebz.connid.connector.cyberark;

import br.tec.ebz.connid.connector.cyberark.operations.UserOperations;
import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import br.tec.ebz.connid.connector.cyberark.utils.BasicTestConnection;
import br.tec.ebz.connid.connector.cyberark.utils.GetUser;
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
    public void shouldCreateActiveUser() {
        String userName = userTest.getUserName();


    }
}
