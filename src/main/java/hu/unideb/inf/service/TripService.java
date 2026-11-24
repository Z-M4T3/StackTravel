package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.TripNotFoundException;
import hu.unideb.inf.exceptions.UserNotFoundException;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.dto.TripDto;
import hu.unideb.inf.model.entity.Trip;
import hu.unideb.inf.model.entity.User;
import hu.unideb.inf.repository.TripRepository;
import hu.unideb.inf.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final EntityMapper mapper;

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository,
            MessageSource messageSource,
            EntityMapper mapper
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.mapper = mapper;
    }

    public TripDto create(Integer userId, TripDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        message("user.not-found", userId)));

        Trip trip = mapper.dtoToTrip(dto);
        Trip saved = tripRepository.save(trip);
        return mapper.tripToDto(saved);
    }

    @Transactional(readOnly = true)
    public TripDto getById(Integer id) {
        return mapper.tripToDto(findTripOrThrow(id));
    }

    public void delete(Integer id) {
        tripRepository.delete(findTripOrThrow(id));
    }

    private Trip findTripOrThrow(Integer id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(
                        message("trip.not-found", id)));
    }

    private String message(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public TripDto update(Integer id, TripDto dto) {
        Trip trip = findTripOrThrow(id);

        trip.setPublicKey(dto.getPublicKey());
        trip.setStartDate(dto.getStartDate());
        trip.setEndDate(dto.getEndDate());

        Trip saved = tripRepository.save(trip);
        return mapper.tripToDto(saved);
    }
}
