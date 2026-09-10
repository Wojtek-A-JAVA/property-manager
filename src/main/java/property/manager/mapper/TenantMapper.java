package property.manager.mapper;

import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import property.manager.config.MapperConfig;
import property.manager.dto.tenant.CreateTenantRequestDto;
import property.manager.dto.tenant.TenantResponseDto;
import property.manager.dto.tenant.UpdateTenantRequestDto;
import property.manager.model.Tenant;

@Mapper(config = MapperConfig.class)
public interface TenantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Tenant toEntity(CreateTenantRequestDto request);

    TenantResponseDto toDto(Tenant tenant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(UpdateTenantRequestDto request, @MappingTarget Tenant tenant);

    List<TenantResponseDto> toDtoList(List<Tenant> tenants);
}
