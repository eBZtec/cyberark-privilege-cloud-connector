package br.tec.ebz.connid.connector.cyberark;

import com.evolveum.polygon.common.GuardedStringAccessor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CyberArkEndpoint {

    private static final Log LOG = Log.getLog(CyberArkEndpoint.class);

    private static final String LOGON_ENDPOINT = "/PasswordVault/API/auth/%s/Logon/";
    private static final String LOGOFF_ENPOINT = "/PasswordVault/API/Auth/Logoff/";

    private final CyberArkConfiguration configuration;
    private CloseableHttpClient httpClient;
    private String token;

    public CyberArkEndpoint(CyberArkConfiguration configuration) throws URISyntaxException {
        this.configuration = configuration;

        initHttpClient();
        logon();
    }

    protected void logon() throws URISyntaxException {
        LOG.info("Staring process to generate access token");

        String logonEndpoint = getApiEndpoint(LOGON_ENDPOINT, configuration.getMethod());
        LOG.info("Logon endpoint defined: {0}", logonEndpoint);

        final URIBuilder logonUriBuilder = createURIBuilder().setPath(logonEndpoint);
        URI logonUri = getUri(logonUriBuilder);

        LOG.info("URI defined: {0}", logonUri.toString());

        JSONObject logonBodyParameters = new JSONObject();
        logonBodyParameters.put("username", configuration.getUser());

        GuardedStringAccessor accessor = new GuardedStringAccessor();
        configuration.getPassword().access(accessor);
        logonBodyParameters.put("password", accessor.getClearString());

        HttpPost post = new HttpPost(logonUri);

        JSONObject tokenResponse = request(post, logonBodyParameters, true, false);

        token = tokenResponse.getString("session_token");
    }

    public void logoff() throws URISyntaxException {
        LOG.info("Logoff session token");

        final URIBuilder logoffUriBuilder = createURIBuilder().setPath(LOGOFF_ENPOINT);
        URI logoffUri = getUri(logoffUriBuilder);
        LOG.info("URI defined: {0}", logoffUri.toString());

        HttpPost post = new HttpPost(logoffUri);
        request(post, false, true);
        LOG.ok("Session logged off successfully");
    }

    public String getApiEndpoint(String endpoint, String method) {
        return String.format(endpoint, method);
    }

    private String getToken() {
        return token;
    }

    public URIBuilder createURIBuilder() {
        return new URIBuilder().setScheme("https").setHost(configuration.getDomain());
    }

    public URI getUri(URIBuilder uriBuilder) {
        URI uri;
        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new ConnectorException("It is not possible to create URI " + e.getLocalizedMessage(), e);
        }
        return uri;
    }

    protected void initHttpClient() {
        final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setRetryStrategy(myRetryHandler);

        // Add Proxy
        // add validate with failover trust

        httpClient = clientBuilder.build();
    }

    private ClassicHttpResponse executeRequest(HttpUriRequestBase request, Boolean requireAuthorization) {
        if (request == null) {
            throw new InvalidAttributeValueException("Request not provided");
        }

        if (requireAuthorization) {
            request.setHeader("Authorization", Base64.getEncoder().encodeToString(getToken().getBytes()));
        }
        request.setHeader("Content-Type", "application/json");

        ClassicHttpResponse response;

        try {
            response = httpClient.execute(request, httpResponse -> {
                processResponseErrors(httpResponse);
                return httpResponse;
            });

            return response;
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            LOG.ok("The exception type: {0}", e.getClass());
            sb.append("It is not possible to execute request: ").append(request.toString()).append(";")
                    .append(e.getLocalizedMessage());
            throw new ConnectorIOException(sb.toString(), e);
        }
    }

    protected JSONObject request(HttpUriRequestBase request, Boolean parseResult, Boolean requireAuthorization) throws URISyntaxException {
        if (request == null) {
            throw new InvalidAttributeValueException("Request not provided or empty");
        }

        String result;

        if (LOG.isOk()) {
            LOG.ok("URL in request: {0}", request.getUri());
            LOG.ok("Enumerating headers");
            for (Header header : request.getHeaders()) {
                LOG.info("Headers.. name,value:{0},{1}", header.getName(), header.getValue());
            }
        }

        try (ClassicHttpResponse response = executeRequest(request, requireAuthorization)) {

            if (response.getCode() == 204) {
                LOG.ok("204 - No Content ");
                return null;
            } else if (response.getCode() == 200 && !parseResult) {
                LOG.ok("200 - OK");
                return null;
            }

            LOG.ok("Response before evaluation: {0}", response.getEntity());

            result = EntityUtils.toString(response.getEntity());
            if (!parseResult) {
                return null;
            }
            return new JSONObject(result);

        } catch (IOException e) {
            throw new ConnectorIOException();
        } catch (ParseException e) {
            throw new InvalidAttributeValueException(e);
        }

    }

    protected JSONObject request(HttpUriRequestBase request, JSONObject body, Boolean parseResult, Boolean requireAuthorization) throws URISyntaxException {
        LOG.info("request URI: {0}", request.getUri());
        if (requireAuthorization) {
            LOG.info("body {0}", body);
        }

        byte[] bodyByte = body.toString().getBytes(StandardCharsets.UTF_8);
        HttpEntity bodyEntity = new ByteArrayEntity(bodyByte, ContentType.APPLICATION_JSON);

        request.setEntity(bodyEntity);
        LOG.info("request {0}", request);

        ClassicHttpResponse response = executeRequest(request, requireAuthorization);

        int statusCode = response.getCode();
        if (statusCode == 201) {
            LOG.ok("201 - Created");
        } else if (statusCode == 204) {
            LOG.ok("204 - no content");
        } else {
            LOG.info("statuscode - {0}", statusCode);
        }


        if (!parseResult) {
            return null;
        }

        // result as output
        HttpEntity responseEntity = response.getEntity();
        try {
            byte[] byteResult = EntityUtils.toByteArray(responseEntity);
            String result = new String(byteResult, StandardCharsets.UTF_8);
            responseClose(response);

            LOG.info("result: {0}", result);
            return new JSONObject(result);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed creating result from HttpEntity: ").append(responseEntity).append(";")
                    .append(e.getLocalizedMessage());
            responseClose(response);
            throw new ConnectorIOException(sb.toString(), e);
        }

    }

    HttpRequestRetryStrategy myRetryHandler = new HttpRequestRetryStrategy() {
        @Override
        public boolean retryRequest(HttpRequest httpRequest, IOException e, int i, HttpContext httpContext) {
            if (i >= 10) {
                // Do not retry if over max retry count
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpUriRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }

            if (e instanceof NoHttpResponseException) {
                LOG.warn("No response from server on " + i + " call");
                return true;
            }

            return false;
        }

        @Override
        public boolean retryRequest(org.apache.hc.core5.http.HttpResponse httpResponse, int i, HttpContext httpContext) {
            return i <= 7 && httpResponse.getCode() >= 500 && httpResponse.getCode() < 600;
        }

        @Override
        public TimeValue getRetryInterval(org.apache.hc.core5.http.HttpResponse httpResponse, int i, HttpContext httpContext) {
            return TimeValue.ofMilliseconds(3000);
        }

    };

    private void processResponseErrors(ClassicHttpResponse response) {
        if (response == null) {
            throw new InvalidAttributeValueException("Response not provided ");
        }

        int statusCode = response.getCode();
        if (statusCode >= 200 && statusCode <= 299) {
            return;
        }

        String responseBody = null;
        try {
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            LOG.warn("cannot read response body: " + e, e);
        }

        String message = "HTTP error " + statusCode + " " + response.getReasonPhrase() + " : " + responseBody;

        if (statusCode == 400) {
            throw new InvalidAttributeValueException(message);
        }
        if (statusCode == 401) {
            throw new ConfigurationException(message);
        }
        if (statusCode == 403 || statusCode == 404 || statusCode == 429 || statusCode == 500 || statusCode == 501) {
            throw new ConnectorException(message);
        }
        if (statusCode == 409) {
            throw new AlreadyExistsException(message);
        }

        throw new ConnectorException(message);

    }

    private void responseClose(ClassicHttpResponse response) {
        try {
            response.close();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed close response: ").append(response);
            LOG.warn(e, sb.toString());
        }
    }
}
