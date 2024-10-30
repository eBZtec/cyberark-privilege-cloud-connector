package br.tec.ebz.connid.connector.cyberark;

import com.evolveum.polygon.common.GuardedStringAccessor;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.OperationOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

        Unirest.config().reset();

        token = null;
    }

    private String getUrl(String endpoint) {
        String sb = "https://" + configuration.getServer() + endpoint;

        return sb;
    }

    private String getEndpoint(String endpoint, String method) {
        return String.format(endpoint, method);
    }

    private void initHttpClient() {
        Unirest.config().reset();
        Unirest.config()
                .connectTimeout(1000)
                .setDefaultHeader("content-type", "application/json")
                .verifySsl(configuration.getVerifySsl())
                .enableCookieManagement(false);
    }

    private String getAccessToken() {
        if (token == null) {
            logon();
        }

        return token;
    }

    private void processResponseErrors(HttpResponse<?> response) {
        int statusCode = response.getStatus();

        LOG.info("Status code {0}", statusCode);

        if (statusCode >= 200 && statusCode <= 299) {
            return;
        }

        if (statusCode == 401) {
            throw new ConnectionFailedException("The request requires user authentication.");
        }

        if (statusCode == 400) {
            throw new InvalidAttributeValueException("Could not process the request, reason: " + response.getBody());
        }

        if (statusCode == 403) {
            throw new ConnectorException("The server received and understood the request, but will not fulfill it. Authorization will not help and the request MUST NOT be repeated");
        }

        if (statusCode == 404) {
            throw new ConnectorException("The server did not find anything that matches the Request-URI. No indication is given of whether the condition is temporary or permanent.");
        }

        if (statusCode == 409) {
            throw new AlreadyExistsException("The request could not be completed due to a conflict with the current state of the resource.");
        }

        if (statusCode == 429) {
            throw new ConnectorException("The user has sent too many requests in a given amount of time (\"rate limiting\").");
        }

        if (statusCode == 500) {
            throw new ConnectorException("The server encountered an unexpected condition which prevented it from fulfilling the request.");
        }

        if (statusCode == 501) {
            throw new ConnectorException("The server does not support this operation due to version incompatibility.");
        }

        throw new ConnectorException("Could not process the request, reason: " + response.getBody());
    }

    public HttpResponse<JsonNode> post(String endpoint, JSONObject body) {
        String url = getUrl(endpoint);
        HttpResponse<JsonNode> response = Unirest
                .post(url)
                .header("Authorization", getAccessToken())
                .body(body)
                .asJson();

        processResponseErrors(response);

        return response;
    }

    public HttpResponse<JsonNode> put(String endpoint, JSONObject body) {
        String url = getUrl(endpoint);
        HttpResponse<JsonNode> response = Unirest
                .put(url)
                .header("Authorization", getAccessToken())
                .body(body)
                .asJson();

        processResponseErrors(response);

        return response;
    }

    public void delete(String endpoint) {
        String url = getUrl(endpoint);

        HttpResponse<JsonNode> response = Unirest
                .delete(url)
                .header("Authorization", getAccessToken())
                .asJson();

        processResponseErrors(response);
    }

    public HttpResponse<JsonNode> get(String endpoint) {
        String url = getUrl(endpoint);
        HttpResponse<JsonNode> response = Unirest
                .get(url)
                .header("Authorization", getAccessToken())
                .asJson();

        processResponseErrors(response);

        return response;
    }

    public List<JSONObject> get(String endpoint, Map<String, Object> query, OperationOptions options) {
        boolean hasMoreData = true;
        String url = getUrl(endpoint);
        List<JSONObject> objects = new ArrayList<>();
        int pageOffset = 0;
        int pageSize = 200;

        if (options.getPageSize() != null) {
            pageSize = options.getPageSize();
        }

        while (hasMoreData) {
            HttpResponse<JsonNode> response = Unirest
                    .get(url)
                    .header("Authorization", getAccessToken())
                    .queryString("pageOffset", pageOffset)
                    .queryString("pageSize", pageSize)
                    .queryString(query)
                    .asJson();
            processResponseErrors(response);

            JsonNode responseBody = response.getBody();
            int total = responseBody.getObject().getInt("Total");

            if (total == 0) {
                hasMoreData = false;
            } else {
                JSONArray users = responseBody.getObject().getJSONArray("Users");

                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    objects.add(user);
                }
                pageOffset += pageSize;

                if (total == 1) {
                    hasMoreData = false;
                }
            }
        }

        LOG.info("Found {0} objects", objects.size());

        return objects;
    }
}
