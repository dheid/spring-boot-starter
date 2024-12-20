package dev.openfga.autoconfigure;

import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties used to configure an {@link dev.openfga.sdk.api.client.OpenFgaClient}
 */
@ConfigurationProperties(prefix = "openfga")
public class OpenFgaProperties implements InitializingBean {

    /**
     * URL to OpenFGA instance.
     * If configured, beans will be initialized.
     */
    private String apiUrl;
    /**
     * The ID of the store to use.
     */
    private String storeId;

    /**
     * The ID of the authorization model to use.
     */
    private String authorizationModelId;

    /**
     * The credentials for the OpenFGA client.
     */
    private Credentials credentials;

    /**
     * The HTTP user agent header to use for requests, default openfga-sdk java/{version}
     */
    private String userAgent;

    /**
     * The read timeout for requests, default 10 seconds
     */
    private Duration readTimeout;

    /**
     * The connect timeout for requests, default 10 seconds
     */
    private Duration connectTimeout;

    /**
     * The maximum number of retries to attempt.
     */
    private Integer maxRetries;

    /**
     * The maximum delay between retries.
     */
    private Duration minimumRetryDelay;

    /**
     * The default headers to use for requests.
     */
    private Map<String, String> defaultHeaders;

    /**
     * The telemetry configuration for the client. See {@link dev.openfga.sdk.telemetry.TelemetryMetric} and {@link dev.openfga.sdk.telemetry.TelemetryAttribute} as well as <a href="https://openfga.dev/docs/getting-started/configure-telemetry">Configure SDK Client Telemetry</a>
     */
    private Map<TelemetryMetric, Map<TelemetryAttribute, Object>> telemetryConfiguration;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public void setAuthorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Duration getMinimumRetryDelay() {
        return minimumRetryDelay;
    }

    public void setMinimumRetryDelay(Duration minimumRetryDelay) {
        this.minimumRetryDelay = minimumRetryDelay;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public Map<TelemetryMetric, Map<TelemetryAttribute, Object>> getTelemetryConfiguration() {
        return telemetryConfiguration;
    }

    public void setTelemetryConfiguration(
            Map<TelemetryMetric, Map<TelemetryAttribute, Object>> telemetryConfiguration) {
        this.telemetryConfiguration = telemetryConfiguration;
    }

    @Override
    public void afterPropertiesSet() {
        validate();
    }

    public void validate() {
        Credentials credentialsProperty = getCredentials();
        if (credentialsProperty != null) {
            CredentialsMethod credentialsMethod = getCredentials().getMethod();
            if (credentialsMethod == null) {
                throw new IllegalStateException("credentials method must not be null");
            }
            CredentialsConfiguration credentialsConfig = credentialsProperty.getConfig();
            switch (credentialsMethod) {
                case NONE -> {}
                case API_TOKEN -> {
                    if (credentialsConfig == null || credentialsConfig.getApiToken() == null) {
                        throw new IllegalStateException(
                                "'API_TOKEN' credentials method specified, but no token specified");
                    }
                }
                case CLIENT_CREDENTIALS -> {
                    if (credentialsConfig == null
                            || credentialsConfig.getApiTokenIssuer() == null
                            || credentialsConfig.getClientId() == null
                            || credentialsConfig.getClientSecret() == null) {
                        throw new IllegalStateException(
                                "'CLIENT_CREDENTIALS' configuration must contain 'client-id', 'client-secret', and 'api-token-issuer'");
                    }
                }
                default -> throw new IllegalStateException(
                        "credentials method must be either 'NONE', 'API_TOKEN', or 'CLIENT_CREDENTIALS'");
            }
        }
    }

    /**
     * {@link dev.openfga.sdk.api.client.OpenFgaClient} credentials properties
     */
    public static class Credentials {

        /**
         * The authentication method to use for the OpenFGA client.
         */
        private CredentialsMethod method;
        /**
         * Authentication config for the OpenFGA client.
         */
        private CredentialsConfiguration config;

        public CredentialsMethod getMethod() {
            return method;
        }

        public void setMethod(CredentialsMethod method) {
            this.method = method;
        }

        public CredentialsConfiguration getConfig() {
            return config;
        }

        public void setConfig(CredentialsConfiguration config) {
            this.config = config;
        }
    }

    /**
     * OpenFgaClient credentials methods
     */
    public enum CredentialsMethod {

        /**
         * No authentication
         */
        NONE,

        /**
         * A static API token. In OAuth2 terms, this indicates an "access token"
         * that will be used to make a request. When used, an {@code api-token} must
         * also be configured.
         */
        API_TOKEN,

        /**
         * OAuth2 client credentials that can be used to acquire an OAuth2 access
         * token. When used, you must also configure {@link CredentialsConfiguration}.
         */
        CLIENT_CREDENTIALS
    }

    /**
     * {@link dev.openfga.sdk.api.client.OpenFgaClient} credentials configuration properties
     */
    public static class CredentialsConfiguration {
        /**
         * The API token used to authenticate the client at OpenFGA.
         * Required, if {@code API_TOKEN} method should be used.
         */
        private String apiToken;
        /**
         * The API token issuer, used as an additional check.
         * Required, if {@code CLIENT_CREDENTIALS} method should be used.
         */
        private String apiTokenIssuer;
        /**
         * The API audience.
         * If configured and if {@code CLIENT_CREDENTIALS} is used,
         * the client will use this audience to obtain an access token.
         */
        private String apiAudience;
        /**
         * The OAuth2 client ID.
         * Required, if {@code CLIENT_CREDENTIALS} method should be used.
         */
        private String clientId;
        /**
         * The OAuth2 client secret.
         * Required, if {@code CLIENT_CREDENTIALS} method should be used.
         */
        private String clientSecret;
        /**
         * Space seperated list of OAuth2 scopes used to
         * obtain an access token.
         */
        private String scopes;

        public String getApiTokenIssuer() {
            return apiTokenIssuer;
        }

        public void setApiTokenIssuer(String apiTokenIssuer) {
            this.apiTokenIssuer = apiTokenIssuer;
        }

        public String getApiAudience() {
            return apiAudience;
        }

        public void setApiAudience(String apiAudience) {
            this.apiAudience = apiAudience;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getApiToken() {
            return apiToken;
        }

        public void setApiToken(String apiToken) {
            this.apiToken = apiToken;
        }

        public String getScopes() {
            return scopes;
        }

        public void setScopes(String scopes) {
            this.scopes = scopes;
        }
    }
}
