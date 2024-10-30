package br.tec.ebz.connid.connector.cyberark.utils;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private static final Log LOG = Log.getLog(PropertiesReader.class);

    private static final Properties PROPERTIES = new Properties();

    private static final String PROPERTIES_FILE = "/properties/test_dev.properties";
    private static final String DOMAIN = "domain";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String METHOD= "method";
    private static final String VERIFY_SSL = "verifySsl";

    public PropertiesReader() {
        try {
            PROPERTIES.load(getClass().getResourceAsStream(PROPERTIES_FILE));
        } catch (FileNotFoundException e) {
            LOG.error(e, "File not found: {0}", e.getLocalizedMessage());
        } catch (IOException e) {
            LOG.error(e, "IO exception occurred {0}", e.getLocalizedMessage());
        } catch (NullPointerException e) {
            LOG.error(e, "Properties file not found", e.getLocalizedMessage());
        }
    }

    public Boolean getVerifySsl() {
        return Boolean.parseBoolean( (String) PROPERTIES.get(VERIFY_SSL));
    }

    public String getDomain() { return (String) PROPERTIES.get(DOMAIN); }

    public String getUser() { return (String) PROPERTIES.get(USER); }

    public GuardedString getPassword() {
        return new GuardedString(((String) PROPERTIES.get(PASSWORD)).toCharArray());
    }

    public String getMethod() {
        if (PROPERTIES.containsKey(METHOD)) {
            return PROPERTIES.getProperty(METHOD);
        }

        return null;
    }
}
