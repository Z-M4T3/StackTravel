package hu.unideb.inf.controller;

import hu.unideb.inf.model.dto.AddCardRequest;
import hu.unideb.inf.model.dto.CardDto;
import hu.unideb.inf.model.dto.TripCardDto;
import hu.unideb.inf.service.CardService;
import hu.unideb.inf.service.TripCardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
public class CardRestController {
    private final CardService cardService;
    private final TripCardService tripCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto create(@Valid @RequestBody CardDto request) {
        return cardService.create(request);
    }

    @PostMapping("/trip/{tripId}")
    public TripCardDto addCardToTrip(@Valid @RequestBody AddCardRequest request, @PathVariable Integer tripId) {
       return tripCardService.addCardToTrip(request, tripId);
    }

    @GetMapping("/{id}")
    public CardDto getById(@PathVariable Integer id) {
        return cardService.getById(id);
    }

    @GetMapping
    public List<CardDto> getByCategory(@RequestParam String category,
                                      @RequestParam(defaultValue = "7") Integer count) {
        return cardService.getNCardsWithCategory(count, category);
    }

    @PutMapping("/{cardId}/trip/{tripId}")
    public TripCardDto updateCardInTrip(@PathVariable Integer tripId, @PathVariable Integer cardId, @Valid @RequestBody TripCardDto dto) {
        return tripCardService.updateCardInTrip(tripId, cardId, dto);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        cardService.deleteById(id);
    }
}
