package br.tec.ebz.connid.connector.cyberark;

import com.evolveum.polygon.common.GuardedStringAccessor;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.Base64;


public class CyberArkEndpoint {

    private static final Log LOG = Log.getLog(CyberArkEndpoint.class);

    private static final String LOGON_ENDPOINT = "/PasswordVault/API/auth/%s/Logon/";
    private static final String LOGOFF_ENPOINT = "/PasswordVault/API/Auth/Logoff/";

    private final CyberArkConfiguration configuration;
    private String token;

    public CyberArkEndpoint(CyberArkConfiguration configuration) {
        this.configuration = configuration;

        initHttpClient();
        logon();
    }

    private void logon() {
        String logonEndpoint = getEndpoint(LOGON_ENDPOINT, configuration.getMethod());
        String url = getUrl(logonEndpoint);

        JSONObject body = new JSONObject();
        body.put("username", configuration.getUser());

        GuardedStringAccessor accessor = new GuardedStringAccessor();
        GuardedString password = configuration.getPassword();
        password.access(accessor);

        body.put("password", accessor.getClearString());

        HttpResponse<String> response = Unirest.post(url).body(body).asString();

        processResponseErrors(response);

        token = response.getBody();
        token = token.replace("\"", "");
    }

    public void logoff() {
        String logoffEndpoint = getEndpoint(LOGOFF_ENPOINT, configuration.getMethod());
        String url = getUrl(logoffEndpoint);

        HttpResponse<kong.unirest.core.JsonNode> response = Unirest.post(url).header("Authorization", token).asJson();
        processResponseErrors(response);
    }

    private String getUrl(String endpoint) {
        String sb = "https://" + configuration.getServer() + endpoint;

        return sb;
    }

    private String getEndpoint(String endpoint, String method) {
        return String.format(endpoint, method);
    }

    private void initHttpClient() {
        Unirest.config()
                .connectTimeout(1000)
                .setDefaultHeader("content-type", "application/json")
                .verifySsl(false)
                .enableCookieManagement(false);
    }

    private void processResponseErrors(HttpResponse<?> response) {
        int statusCode = response.getStatus();

        LOG.info("Status code {0}", statusCode);

        if (statusCode >= 200 && statusCode <= 299) {
            return;
        }

        if (statusCode == 401) {
            throw new ConnectorException("The request requires user authentication.");
        }

    }
}
