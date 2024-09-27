package br.tec.ebz.connid.connector.cyberark;

import br.tec.ebz.connid.connector.cyberark.utils.BasicTestConnection;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestOpTest {

    private ConnectorFacade connectorFacade;

    @BeforeTest
    public void init() {
        BasicTestConnection basicTestConnection = new BasicTestConnection();
        connectorFacade = basicTestConnection.getTestConnection();
    }

    @Test
    public void shouldConnectToCyberArkAPISuccessfully() {
        connectorFacade.test();
    }
}
