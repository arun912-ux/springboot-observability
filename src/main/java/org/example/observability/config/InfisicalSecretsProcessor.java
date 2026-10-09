package org.example.observability.config;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.models.Secret;
import com.infisical.sdk.util.InfisicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class InfisicalSecretsProcessor implements EnvironmentPostProcessor {

    @Value("INFISICAL_CLIENT_SECRET")
    private String clientSecret;
    @Value("INFISICAL_CLIENT_ID")
    private String clientId;


    /**
     * Post-process the given {@code environment}.
     *
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println(">>> InfisicalSecretsProcessor is running! <<<");
        DeferredLog deferredLog = new DeferredLog();
        deferredLog.info(">>>> InfisicalSecretsProcessor is running! <<<<");

        Map<String, Object> secretMap = getSecretMap();
        log.info("Infisical Secrets processor: {}", secretMap);
        environment.getPropertySources().addFirst(new MapPropertySource("infisical_secrets", secretMap));

    }


    public Map<String, Object> getSecretMap() {

        SdkConfig sdkConfig = new SdkConfig.Builder().build();
        InfisicalSdk sdk = new InfisicalSdk(sdkConfig);

        Map<String, Object> secretMap = new HashMap<>();

        try {
            sdk.Auth().UniversalAuthLogin(clientId, clientSecret);

            List<Secret> secrets = sdk.Secrets()
                    .ListSecrets(
                            "1973140a-ad70-4829-8f95-4210a57d3a58",
                            "dev",
                            "/",
                            false,
                            true,
                            true,
                            false
                    );

            secrets.forEach(s -> secretMap.put(s.getSecretKey(), s.getSecretValue()));

        } catch (InfisicalException e) {
            log.error("Unable to retrieve secrets from Infisical");
            throw new RuntimeException(e);
        }

        return secretMap;
    }


}
