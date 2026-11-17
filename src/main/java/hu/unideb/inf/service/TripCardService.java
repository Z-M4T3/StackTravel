package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.*;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.dto.AddCardRequest;
import hu.unideb.inf.model.dto.TripCardDto;
import hu.unideb.inf.model.entity.Trip;
import hu.unideb.inf.model.entity.TripCard;
import hu.unideb.inf.repository.*;
import hu.unideb.inf.repository.TripCardRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import hu.unideb.inf.model.entity.Card;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class TripCardService {
    private final TripCardRepository tripCardRepository;
    private final TripRepository tripRepository;
    private final CardService cardService;
    private final EntityMapper mapper;
    private final MessageSource messageSource;

    public TripCardService(
            TripCardRepository tripCardRepository,
            TripRepository tripRepository,
            CardRepository cardRepository,
            CardService cardService,
            EntityMapper mapper,
            MessageSource messageSource) {
        this.tripCardRepository = tripCardRepository;
        this.tripRepository = tripRepository;
        this.cardService = cardService;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    public TripCardDto addCardToTrip(Integer tripId, AddCardRequest req) {
        Trip trip = findTripOrThrow(tripId);

        long days = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
        if (req.getCardDay() > days) {
            throw new CardDayDoesntFitIntoTripException(message("card-day.doesnt-fit-into-trip"));
        }

        Card card = cardService.getOrCreateEntity(req.getCardId(), req.getNewCard());

        TripCard.Id key = new TripCard.Id(tripId, card.getId());
        if (tripCardRepository.existsById(key)) {
            throw new CardAlreadyInTripException(message("card.already-added"));
        }

        TripCard tripCard = new TripCard();
        tripCard.setId(key);
        tripCard.setTrip(trip);
        tripCard.setCard(card);
        tripCard.setCardDay(req.getCardDay());
        tripCard.setStartTime(req.getStartTime());
        tripCard.setDuration(req.getDuration());

        return mapper.tripCardToDto(tripCardRepository.save(tripCard));
    }

    @Transactional(readOnly = true)
    public List<TripCardDto> getAllCardsAddedToTrip(Integer tripId) {
        findTripOrThrow(tripId);

        return tripCardRepository.findByIdTripId(tripId).stream()
                .map(mapper::tripCardToDto)
                .toList();
    }

    public TripCardDto updateCardInTrip(Integer tripId, Integer cardId, TripCardDto dto) {
        TripCard tripCard = tripCardRepository.findById(new TripCard.Id(tripId, cardId))
                .orElseThrow(() -> new TripCardNotFoundException(message("trip-card.not-found", tripId, cardId)));

        Trip trip = tripCard.getTrip();
        long days = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
        if (dto.getCardDay() > days) {
            throw new CardDayDoesntFitIntoTripException(message("card_day.doesnt-fit-into-trip"));
        }

        tripCard.setCardDay(dto.getCardDay());
        tripCard.setStartTime(dto.getStartTime());
        tripCard.setDuration(dto.getDuration());

        return mapper.tripCardToDto(tripCard);
    }

    public void deleteCardFromTrip(Integer tripId, Integer cardId) {
        TripCard.Id key = new TripCard.Id(tripId, cardId);
        TripCard tripCard = tripCardRepository.findById(key)
                .orElseThrow(() -> new TripCardNotFoundException(message("trip-card.not-found", tripId, cardId)));
        tripCardRepository.delete(tripCard);
    }

    // helpers
    private String message(String code, Object... arguments){
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }

    private Trip findTripOrThrow(Integer id) {
        return tripRepository.findById(id)
                .orElseThrow(() ->
                        new TripNotFoundException(message("trip.not-found", id)));
    }
}
