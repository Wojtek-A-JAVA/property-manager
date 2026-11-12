package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.lease.CreateLeaseRequestDto;
import property.manager.dto.lease.LeaseResponseDto;
import property.manager.model.Lease;
import property.manager.model.Unit;

@Mapper(config = MapperConfig.class)
public interface LeaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "units", ignore = true)
    Lease toEntity(CreateLeaseRequestDto request);

    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "unitIds", source = "units")
    LeaseResponseDto toDto(Lease lease);

    List<LeaseResponseDto> toDtoList(List<Lease> leases);

    default List<Long> map(List<Unit> units) {
        return units.stream().map(Unit::getId).toList();
    }
}
