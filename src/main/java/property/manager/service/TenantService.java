package property.manager.service;

import java.util.List;
import property.manager.dto.tenant.CreateTenantRequestDto;
import property.manager.dto.tenant.TenantFilterDto;
import property.manager.dto.tenant.TenantResponseDto;
import property.manager.dto.tenant.UpdateTenantRequestDto;

public interface TenantService {
    TenantResponseDto createTenant(CreateTenantRequestDto request);

    TenantResponseDto getTenant(Long id);

    TenantResponseDto updateTenant(Long id, UpdateTenantRequestDto request);

    TenantResponseDto toggleActiveStatus(Long id);

    List<TenantResponseDto> getTenants(TenantFilterDto filter);
}
