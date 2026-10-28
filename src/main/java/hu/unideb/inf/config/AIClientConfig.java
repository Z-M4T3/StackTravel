package hu.unideb.inf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AIClientConfig {
    @Bean
    RestClient restClient(RestClient.Builder builder,
                          @Value("${AI_BASE_URL:http://127.0.0.1:8090}") String aiBaseUrl) {
        return builder
                .baseUrl(aiBaseUrl)
                .build();
    }
}
