package org.example.observability.config;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.models.Secret;
import com.infisical.sdk.util.InfisicalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {@link EnvironmentPostProcessor} implementation that fetches application secrets from Infisical
 * during the early environment preparation phase before the Spring ApplicationContext is initialized.
 * <p>
 * Reads the {@code INFISICAL_CLIENT_ID} and {@code INFISICAL_CLIENT_SECRET} environment variables,
 * connects to Infisical via Universal Auth, pulls configured secrets for the project and environment,
 * and adds them with highest precedence to Spring's {@link ConfigurableEnvironment} property sources.
 */
public class InfisicalSecretsProcessor implements EnvironmentPostProcessor {


    private String clientSecret;
    private String clientId;
    private String projectId = "1973140a-ad70-4829-8f95-4210a57d3a58";
    private String envSlug = "dev";

    DeferredLog deferredLog = new DeferredLog();

    /**
     * Post-processes the Spring environment prior to application context refresh.
     * <p>
     * Extracts Infisical client credentials from the system environment, validates their presence,
     * calls {@link #getSecretMap(String, String, String, String)} to query Infisical, and registers
     * the returned secrets as a {@link MapPropertySource} named {@code "infisical_secrets"}.
     *
     * @param environment the {@link ConfigurableEnvironment} being prepared
     * @param application the {@link SpringApplication} instance to which the environment belongs
     * @throws RuntimeException if credentials are blank or if retrieval from Infisical fails
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        clientId = environment.getProperty("INFISICAL_CLIENT_ID");
        clientSecret = environment.getProperty("INFISICAL_CLIENT_SECRET");
        System.out.println(">> Checking Infisical Secrets Processor - Client ID: " + clientId);

        if (StringUtils.isBlank(clientId) || StringUtils.isBlank(clientSecret)) {
            deferredLog.error("Infisical clientId or clientSecret is blank. Please set the environment variables INFISICAL_CLIENT_ID and INFISICAL_CLIENT_SECRET.");
            throw new RuntimeException("Infisical clientId or clientSecret is blank. Please set the environment variables INFISICAL_CLIENT_ID and INFISICAL_CLIENT_SECRET.");
        }

        System.out.println(">>> InfisicalSecretsProcessor is running! <<<");
        deferredLog.info(">>>> InfisicalSecretsProcessor is running! <<<<");

        Map<String, Object> secretMap = getSecretMap(clientId, clientSecret, projectId, envSlug);
        deferredLog.info("Infisical Secrets processor: {}" + secretMap);
        environment.getPropertySources().addFirst(new MapPropertySource("infisical_secrets", secretMap));

    }


    /**
     * Authenticates with Infisical via Universal Auth and retrieves secret key-value pairs.
     *
     * @param clientId     the Infisical Machine Identity Client ID
     * @param clientSecret the Infisical Machine Identity Client Secret
     * @param projectId    the Infisical Project / Workspace UUID
     * @param envSlug      the environment slug (e.g., "dev", "prod", "staging")
     * @return a {@link Map} of secret names to secret values
     * @throws RuntimeException wrapping {@link InfisicalException} if authentication or fetching fails
     */
    public Map<String, Object> getSecretMap(String clientId, String clientSecret, String projectId, String envSlug) {

        SdkConfig sdkConfig = new SdkConfig.Builder().build();
        InfisicalSdk sdk = new InfisicalSdk(sdkConfig);

        Map<String, Object> secretMap = new HashMap<>();

        try {
            // Authenticate with Infisical using the provided clientId and clientSecret
            sdk.Auth().UniversalAuthLogin(clientId, clientSecret);

            // Retrieve secrets from Infisical
            List<Secret> secrets = sdk.Secrets()
                    .ListSecrets(
                            projectId,
                            envSlug,
                            "/",
                            false,
                            true,
                            true,
                            false
                    );

            secrets.forEach(s -> secretMap.put(s.getSecretKey(), s.getSecretValue()));

        } catch (InfisicalException e) {
            deferredLog.error("Unable to retrieve secrets from Infisical", e);
            throw new RuntimeException(e);
        }

        return secretMap;
    }


}
