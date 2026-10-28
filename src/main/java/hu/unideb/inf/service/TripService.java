package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.CardNotFoundException;
import hu.unideb.inf.exceptions.TripNotFoundException;
import hu.unideb.inf.exceptions.UserNotFoundException;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.dto.TripDto;
import hu.unideb.inf.model.entity.Card;
import hu.unideb.inf.model.entity.Trip;
import hu.unideb.inf.model.entity.User;
import hu.unideb.inf.repository.CardRepository;
import hu.unideb.inf.repository.TripRepository;
import hu.unideb.inf.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final MessageSource messageSource;
    private final EntityMapper mapper;

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository,
            CardRepository cardRepository,
            MessageSource messageSource,
            EntityMapper mapper
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.messageSource = messageSource;
        this.mapper = mapper;
    }

    public TripDto create(Integer userId, TripDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        message("user.not-found", userId)));

        Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new CardNotFoundException(
                        message("card.not-found", dto.getCardId())));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setCard(card);
        if (dto.getIndex() != null) {
            trip.setIndex(dto.getIndex());
        }
        // createdAt-ot a @PrePersist állítja be

        Trip saved = tripRepository.save(trip);
        return mapper.tripToDto(saved);
    }

    @Transactional(readOnly = true)
    public TripDto getById(Integer id, Integer userId) {
        return mapper.tripToDto(findTrip(id, userId));
    }

    @Transactional(readOnly = true)
    public List<TripDto> getAllByUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(message("user.not-found", userId));
        }
        return tripRepository.findAllByUserId(userId).stream()
                .map(mapper::tripToDto)
                .toList();
    }

    public void delete(Integer id, Integer userId) {
        tripRepository.delete(findTrip(id, userId));
    }

    private Trip findTrip(Integer id, Integer userId) {
        return tripRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TripNotFoundException(
                        message("trip.not-found", id)));
    }

    private String message(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public TripDto update(Integer id, Integer userId, TripDto dto) {
        Trip trip = findTrip(id, userId);

        if (dto.getIndex() != null) {
            trip.setIndex(dto.getIndex());
        }

        if (dto.getCardId() != null) {
            Card card = cardRepository.findById(dto.getCardId())
                    .orElseThrow(() -> new CardNotFoundException(
                            message("card.not-found", dto.getCardId())));
            trip.setCard(card);
        }

        // createdAt nem módosítható (updatable = false), ezért a DTO-ból nem is használjuk

        Trip saved = tripRepository.save(trip);
        return mapper.tripToDto(saved);
    }
}
