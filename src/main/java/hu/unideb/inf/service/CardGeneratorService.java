package hu.unideb.inf.service;

import hu.unideb.inf.model.dto.AICardResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class CardGeneratorService {
    private final RestClient client;

    public CardGeneratorService(RestClient client) {
        this.client = client;
    }

    public List<AICardResponseDto> generateCards(String destination, String category, Integer count) {
        return client.get()
                .uri("/generate/?destination={d}&category={c}&count={n}", destination, category, count)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AICardResponseDto>>() {});
    }
}
