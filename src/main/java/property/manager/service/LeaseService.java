package property.manager.service;

import java.util.List;
import property.manager.dto.lease.CreateLeaseRequestDto;
import property.manager.dto.lease.LeaseFilterDto;
import property.manager.dto.lease.LeaseResponseDto;
import property.manager.dto.lease.LeaseStatusRequestDto;

public interface LeaseService {
    LeaseResponseDto createLease(CreateLeaseRequestDto request);

    LeaseResponseDto getLease(Long id);

    List<LeaseResponseDto> getLeases(LeaseFilterDto filter);

    LeaseResponseDto changeLeaseStatus(Long id, LeaseStatusRequestDto request);
}
