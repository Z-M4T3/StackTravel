package hu.unideb.inf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class AIClientConfig {
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://127.0.0.1:8000")
                .build();
    }
}