package hu.unideb.inf.mapper;

import hu.unideb.inf.model.dto.*;
import hu.unideb.inf.model.entity.*;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {
    UserDto userToDto(User user);

    @Mapping(target = "id", ignore = true)
    User dtoToUser(UserDto dto);

    TripDto tripToDto(Trip trip);

    @Mapping(target = "id", ignore = true)
    Trip dtoToTrip(TripDto dto);

    CardDto cardToDto(Card card);

    @Mapping(target = "id", ignore = true)
    Card dtoToCard(CardDto dto);

    CategoryDto categoryToDto(Category category);

    Category dtoToCategory(CategoryDto dto);

    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "id.tripId", target = "tripId")
    UserTripDto userTripToDto(UserTrip userTrip);

    @Mapping(source = "tripId", target = "id.tripId")
    @Mapping(source = "userId", target = "id.userId")
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserTrip dtoToUserTrip(UserTripDto dto);

    @Mapping(source = "id.tripId", target = "tripId")
    @Mapping(source = "id.cardId", target = "cardId")
    TripCardDto tripCardToDto(TripCard tripCard);

    @Mapping(source = "tripId", target = "id.tripId")
    @Mapping(source = "cardId", target = "id.cardId")
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "card", ignore = true)
    TripCard dtoToTripCard(TripCardDto dto);

}