package property.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.user.UserResponseDto;
import property.manager.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "role", source = "role.roleName")
    UserResponseDto toDto(User user);
}
