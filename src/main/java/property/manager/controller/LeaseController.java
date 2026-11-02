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
import property.manager.dto.lease.CreateLeaseRequestDto;
import property.manager.dto.lease.LeaseFilterDto;
import property.manager.dto.lease.LeaseResponseDto;
import property.manager.dto.lease.LeaseStatusRequestDto;
import property.manager.service.LeaseService;

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
public class LeaseController {

    private final LeaseService leaseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public LeaseResponseDto createLease(@RequestBody @Valid CreateLeaseRequestDto request) {
        return leaseService.createLease(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public LeaseResponseDto getLease(@PathVariable Long id) {
        return leaseService.getLease(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<LeaseResponseDto> getLeases(@ModelAttribute LeaseFilterDto filter) {
        return leaseService.getLeases(filter);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public LeaseResponseDto changeLeaseStatus(
            @PathVariable Long id, @RequestBody @Valid LeaseStatusRequestDto request) {
        return leaseService.changeLeaseStatus(id, request);
    }
}
