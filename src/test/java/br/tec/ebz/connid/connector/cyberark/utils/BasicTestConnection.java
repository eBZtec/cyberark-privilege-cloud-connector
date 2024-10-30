package br.tec.ebz.connid.connector.cyberark.utils;

import br.tec.ebz.connid.connector.cyberark.CyberArkConfiguration;
import br.tec.ebz.connid.connector.cyberark.CyberArkConnector;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.test.common.TestHelpers;

public class BasicTestConnection {

    private final PropertiesReader propertiesReader;

    public BasicTestConnection() {
        propertiesReader = new PropertiesReader();
    }

    public ConnectorFacade getTestConnection() {
        CyberArkConfiguration configuration = new CyberArkConfiguration();
        configuration.setServer(propertiesReader.getDomain());
        configuration.setUser(propertiesReader.getUser());
        configuration.setPassword(propertiesReader.getPassword());

        if (propertiesReader.getMethod() != null) {
            configuration.setMethod(propertiesReader.getMethod());
        }

        if (propertiesReader.getVerifySsl() != null) {
            configuration.setVerifySsl(propertiesReader.getVerifySsl());
        }

        return getTestConnection(configuration);
    }

    public ConnectorFacade getTestConnection(CyberArkConfiguration configuration) {
        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();

        APIConfiguration impl = TestHelpers.createTestConfiguration(CyberArkConnector.class, configuration);

        impl.getResultsHandlerConfiguration().setEnableAttributesToGetSearchResultsHandler(false);
        impl.getResultsHandlerConfiguration().setEnableCaseInsensitiveFilter(false);
        impl.getResultsHandlerConfiguration().setEnableFilteredResultsHandler(false);
        impl.getResultsHandlerConfiguration().setEnableNormalizingResultsHandler(false);
        impl.getResultsHandlerConfiguration().setFilteredResultsHandlerInValidationMode(false);

        return factory.newInstance(impl);
    }
}
