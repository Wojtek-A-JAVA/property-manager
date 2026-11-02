package property.manager.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.tenant.CreateTenantRequestDto;
import property.manager.dto.tenant.TenantFilterDto;
import property.manager.dto.tenant.TenantResponseDto;
import property.manager.dto.tenant.UpdateTenantRequestDto;
import property.manager.service.TenantService;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public TenantResponseDto createTenant(@RequestBody @Valid CreateTenantRequestDto request) {
        return tenantService.createTenant(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public TenantResponseDto getTenant(@PathVariable Long id) {
        return tenantService.getTenant(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantResponseDto updateTenant(
            @PathVariable Long id, @RequestBody @Valid UpdateTenantRequestDto request) {
        return tenantService.updateTenant(id, request);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantResponseDto toggleTenantActiveStatus(@PathVariable Long id) {
        return tenantService.toggleActiveStatus(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<TenantResponseDto> getTenants(@ModelAttribute TenantFilterDto filter) {
        return tenantService.getTenants(filter);
    }
}
