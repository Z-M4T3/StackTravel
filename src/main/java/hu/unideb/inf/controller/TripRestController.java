package hu.unideb.inf.controller;

import hu.unideb.inf.model.dto.TripCardDto;
import hu.unideb.inf.model.dto.TripDto;
import hu.unideb.inf.service.TripCardService;
import hu.unideb.inf.service.TripService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/api/trips", produces = MediaType.APPLICATION_JSON_VALUE)
public class TripRestController {
    private final TripService tripService;
    private final TripCardService tripCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripDto create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TripDto request) {
        return tripService.create(Integer.valueOf(Objects.requireNonNull(jwt.getSubject())), request);
    }

    @GetMapping("/{id}")
    public TripDto getById(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id) {
        return tripService.getById(id);
    }

    @GetMapping("/{id}/cards")
    public List<TripCardDto> getAllCardsAddedToTrip(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id) {
        return tripCardService.getAllCardsAddedToTrip(id);
    }


    @PutMapping("/{id}")
    public TripDto update(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id,
                          @Valid @RequestBody TripDto request) {
        return tripService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id) {
        tripService.delete(id);
    }

    @DeleteMapping("/{tripId}/card/{cardId}")
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer tripId, @PathVariable Integer cardId) {
        tripCardService.deleteCardFromTrip(tripId, cardId);
    }
}
