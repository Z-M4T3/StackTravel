package hu.unideb.inf.mapper;

import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.model.entiry.User;
import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {

    UserDto userToDto(User user);
    User dtoToUser(UserDto dto);
}