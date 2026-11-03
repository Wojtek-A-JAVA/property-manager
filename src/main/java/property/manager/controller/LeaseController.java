package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import property.manager.dto.lease.CreateLeaseRequestDto;
import property.manager.dto.lease.LeaseFilterDto;
import property.manager.dto.lease.LeaseResponseDto;
import property.manager.dto.lease.LeaseStatusRequestDto;
import property.manager.service.LeaseService;

@Tag(name = "05. Leases")
@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
public class LeaseController {

    private final LeaseService leaseService;

    @Operation(summary = "Create lease",
            description = "Creates a new lease for a unit and tenant")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public LeaseResponseDto createLease(@RequestBody @Valid CreateLeaseRequestDto request) {
        return leaseService.createLease(request);
    }

    @Operation(summary = "Get property by id",
            description = "Returns a property with the given id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public LeaseResponseDto getLease(@PathVariable Long id) {
        return leaseService.getLease(id);
    }

    @Operation(summary = "Get properties",
            description = "Returns properties filtered by the provided parameters")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<LeaseResponseDto> getLeases(@ModelAttribute LeaseFilterDto filter) {
        return leaseService.getLeases(filter);
    }

    @Operation(summary = "Change lease status", description =
            "Changes the status of an existing lease."
                    + " This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public LeaseResponseDto changeLeaseStatus(
            @PathVariable Long id, @RequestBody @Valid LeaseStatusRequestDto request) {
        return leaseService.changeLeaseStatus(id, request);
    }
}
