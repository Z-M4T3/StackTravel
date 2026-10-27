package hu.unideb.inf.mapper;

import hu.unideb.inf.model.dto.TripDto;
import hu.unideb.inf.model.dto.CardDto;
import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.model.entity.Card;
import hu.unideb.inf.model.entity.Trip;
import hu.unideb.inf.model.entity.User;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {
    UserDto userToDto(User user);

    @Mapping(target = "id", ignore = true)
    User dtoToUser(UserDto dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "card.id", target = "cardId")
    TripDto tripToDto(Trip trip);

    CardDto cardToDto(Card card);

    @Mapping(target = "id", ignore = true)
    Card dtoToCard(CardDto dto);
}