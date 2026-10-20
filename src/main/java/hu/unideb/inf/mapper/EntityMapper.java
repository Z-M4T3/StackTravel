package hu.unideb.inf.mapper;

import hu.unideb.inf.model.dto.UserDto;
import hu.unideb.inf.model.entity.User;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {
    UserDto userToDto(User user);

    @Mapping(target = "id", ignore = true)
    User dtoToUser(UserDto dto);
}