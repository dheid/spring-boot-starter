package dev.openfga.autoconfigure;

import dev.openfga.OpenFga;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;

/**
 * Configures an {@code openFgaClient} and {@code openFga} beans based
 * on configuration values. The beans will only be created if the
 * {@link OpenFgaClient} is present on the classpath, and the
 * {@code openfga.api-url} is specified.
 */
@AutoConfiguration
@ConditionalOnFgaProperties
@EnableConfigurationProperties(OpenFgaProperties.class)
public class OpenFgaAutoConfiguration {

    private final OpenFgaProperties openFgaProperties;

    public OpenFgaAutoConfiguration(OpenFgaProperties openFgaProperties) {
        this.openFgaProperties = openFgaProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientConfiguration fgaConfig() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        PropertyMapper map = PropertyMapper.get();
        map.from(openFgaProperties::getCredentials)
                .whenNonNull()
                .as(OpenFgaAutoConfiguration::toCredentials)
                .to(clientConfiguration::credentials);
        map.from(openFgaProperties::getApiUrl).whenHasText().to(clientConfiguration::apiUrl);
        map.from(openFgaProperties::getStoreId).whenHasText().to(clientConfiguration::storeId);
        map.from(openFgaProperties::getAuthorizationModelId)
                .whenHasText()
                .to(clientConfiguration::authorizationModelId);
        map.from(openFgaProperties::getUserAgent).whenHasText().to(clientConfiguration::userAgent);
        map.from(openFgaProperties::getReadTimeout).whenNonNull().to(clientConfiguration::readTimeout);
        map.from(openFgaProperties::getConnectTimeout).whenNonNull().to(clientConfiguration::connectTimeout);
        map.from(openFgaProperties::getMaxRetries).whenNonNull().to(clientConfiguration::maxRetries);
        map.from(openFgaProperties::getMinimumRetryDelay).whenNonNull().to(clientConfiguration::minimumRetryDelay);
        map.from(openFgaProperties::getDefaultHeaders).whenNonNull().to(clientConfiguration::defaultHeaders);
        map.from(openFgaProperties::getTelemetryConfiguration)
                .whenNonNull()
                .as(OpenFgaAutoConfiguration::toTelemetryConfiguration)
                .to(clientConfiguration::telemetryConfiguration);
        return clientConfiguration;
    }

    private static Credentials toCredentials(OpenFgaProperties.Credentials credentialsProperties) {
        Credentials credentials = new Credentials();
        if (OpenFgaProperties.CredentialsMethod.API_TOKEN == credentialsProperties.getMethod()) {
            credentials.setCredentialsMethod(CredentialsMethod.API_TOKEN);
            credentials.setApiToken(
                    new ApiToken(credentialsProperties.getConfig().getApiToken()));
        } else if (OpenFgaProperties.CredentialsMethod.CLIENT_CREDENTIALS.equals(credentialsProperties.getMethod())) {
            ClientCredentials clientCredentials = new ClientCredentials()
                    .clientId(credentialsProperties.getConfig().getClientId())
                    .clientSecret(credentialsProperties.getConfig().getClientSecret())
                    .apiTokenIssuer(credentialsProperties.getConfig().getApiTokenIssuer())
                    .apiAudience(credentialsProperties.getConfig().getApiAudience())
                    .scopes(credentialsProperties.getConfig().getScopes());
            credentials.setCredentialsMethod(CredentialsMethod.CLIENT_CREDENTIALS);
            credentials.setClientCredentials(clientCredentials);
        }
        return credentials;
    }

    private static TelemetryConfiguration toTelemetryConfiguration(
            Map<TelemetryMetric, Map<TelemetryAttribute, Object>> telemetryConfiguration) {
        return new TelemetryConfiguration()
                .metrics(telemetryConfiguration.entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey().getMetric(), metric -> metric.getValue().entrySet().stream()
                                        .collect(Collectors.toMap(
                                                metricConfig ->
                                                        metricConfig.getKey().getAttribute(),
                                                metricConfig -> Optional.ofNullable(metricConfig.getValue()))))));
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenFgaClient fgaClient(ClientConfiguration configuration) {
        try {
            return new OpenFgaClient(configuration);
        } catch (FgaInvalidParameterException e) {
            throw new BeanCreationException("Failed to create OpenFgaClient", e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenFga fga(OpenFgaClient openFgaClient) {
        return new OpenFga(openFgaClient);
    }
}
