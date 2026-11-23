package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.*;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.dto.UserTripDto;
import hu.unideb.inf.model.entity.Trip;
import hu.unideb.inf.model.entity.User;
import hu.unideb.inf.model.entity.UserTrip;
import hu.unideb.inf.repository.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@Transactional
public class UserTripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final UserTripRepository userTripRepository;
    private final EntityMapper mapper;
    private final MessageSource messageSource;
    private UserService userService;

    public UserTripService(
            TripRepository tripRepository,
            UserRepository userRepository,
            UserTripRepository userTripRepository,
            EntityMapper mapper,
            MessageSource messageSource) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.userTripRepository = userTripRepository;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    public UserTripDto addTripToUser(Integer userId, Integer tripId) {
        Trip trip = findTripOrThrow(tripId);
        User user = findUserOrThrow(userId);

        UserTrip.Id key = new UserTrip.Id(tripId, userId);

        UserTrip userTrip = new UserTrip();
        userTrip.setId(key);
        userTrip.setTrip(trip);
        userTrip.setUser(user);
        userTrip.setPermissionLevel("VIEWER");

        return mapper.userTripToDto(userTripRepository.save(userTrip));
    }

    public UserTripDto getUserTrip(Integer userId, Integer tripId) {
        findTripOrThrow(tripId);
        findUserOrThrow(userId);

        UserTrip.Id key = new UserTrip.Id(userId, tripId);
        return userTripRepository.findById(key)
                .map(mapper::userTripToDto)
                .orElseThrow(()-> new UserTripNotFoundException(message("user-trip.not-found", userId, tripId)));
    }

    public List<UserTripDto> getAllUserTrip(Integer userId) {
        User user = findUserOrThrow(userId);

        return userTripRepository.findByUser(user)
                .stream()
                .map(mapper::userTripToDto)
                .toList();
    }

    public UserTripDto modifyPermissionLevelOnTrip(Integer userId, Integer tripId, String permissionLevel) {
        findUserOrThrow(userId);
        findTripOrThrow(tripId);

        if (permissionLevel == null) { throw new InvalidPermissionLevelException(message("permission.invalid", permissionLevel)); }
        String perm = permissionLevel.toUpperCase();

        UserTrip userTrip = findUserTripOrThrow(userId, tripId);

        Set<String> validPermissionLevels = Set.of("OWNER", "EDITOR", "VIEWER");
        if (!validPermissionLevels.contains(perm)) {
            throw new InvalidPermissionLevelException(message("permission.invalid", perm));
        }

        userTrip.setPermissionLevel(perm);
        UserTrip saved = userTripRepository.save(userTrip);
        return mapper.userTripToDto(saved);
    }

    public void deleteTripFromUser(Integer userId, Integer tripId) {
        findUserOrThrow(userId);
        findTripOrThrow(tripId);

        UserTrip userTrip = findUserTripOrThrow(userId, tripId);
        userTripRepository.delete(userTrip);
    }

    // helpers
    private String message(String code, Object... arguments){
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }

    private UserTrip findUserTripOrThrow(Integer userId, Integer tripId) {
        UserTrip.Id key = new UserTrip.Id(userId, tripId);
        return userTripRepository.findById(key)
                .orElseThrow(() -> new UserTripNotFoundException(message("user-trip.not-found", userId, tripId)));
    }

    private User findUserOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(message("user.not-found", id)));
    }

    private Trip findTripOrThrow(Integer id) {
        return tripRepository.findById(id)
                .orElseThrow(() ->
                        new TripNotFoundException(message("trip.not-found", id)));
    }
}
