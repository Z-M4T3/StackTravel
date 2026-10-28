package hu.unideb.inf.controller;

import hu.unideb.inf.model.dto.AICardResponseDto;
import hu.unideb.inf.service.CardGeneratorService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Validated
public class CardGeneratorController {
    private final CardGeneratorService service;

    public CardGeneratorController(CardGeneratorService service) {
        this.service = service;
    }

    @GetMapping("/generate")
    public List<AICardResponseDto> generate(
            @RequestParam @NotBlank @Size(max = 100) String destination,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "7") Integer count) {
        return service.generateCards(destination, category, count);
    }
}
