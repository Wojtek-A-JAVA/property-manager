package property.manager.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import property.manager.dto.lease.CreateLeaseRequestDto;
import property.manager.dto.lease.LeaseFilterDto;
import property.manager.dto.lease.LeaseResponseDto;
import property.manager.dto.lease.LeaseStatusRequestDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InactiveEntityException;
import property.manager.exception.InvalidLeaseDataException;
import property.manager.mapper.LeaseMapper;
import property.manager.model.Lease;
import property.manager.model.LeaseStatus;
import property.manager.model.Tenant;
import property.manager.model.Unit;
import property.manager.repository.LeaseRepository;
import property.manager.repository.TenantRepository;
import property.manager.repository.UnitRepository;
import property.manager.service.LeaseService;
import property.manager.specification.LeaseSpecification;

@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {

    private final TenantRepository tenantRepository;
    private final UnitRepository unitRepository;
    private final LeaseRepository leaseRepository;
    private final LeaseMapper leaseMapper;

    @Override
    public LeaseResponseDto createLease(CreateLeaseRequestDto request) {
        checkDates(request.startDate(), request.endDate());
        Tenant tenant = getActiveTenant(request.tenantId());
        Unit unit = getAvailableUnit(request);
        Lease lease = leaseMapper.toEntity(request);
        lease.setTenant(tenant);
        lease.setUnit(unit);
        if (lease.getLeaseStatus() == null) {
            lease.setLeaseStatus(LeaseStatus.ACTIVE);
        }
        Lease savedLease = leaseRepository.save(lease);
        return leaseMapper.toDto(savedLease);
    }

    @Override
    public LeaseResponseDto getLease(Long id) {
        Lease lease = findLeaseById(id);
        return leaseMapper.toDto(lease);
    }

    @Override
    public List<LeaseResponseDto> getLeases(LeaseFilterDto filter) {
        checkDates(filter.startDate(), filter.endDate());
        Specification<Lease> specification = LeaseSpecification.hasStatus(filter.status())
                .and(LeaseSpecification.hasTenantId(filter.tenantId()))
                .and(LeaseSpecification.hasUnitId(filter.unitId()))
                .and(LeaseSpecification.hasVatRate(filter.vatRate()))
                .and(LeaseSpecification.overlapsPeriod(filter.startDate(), filter.endDate()))
                .and(LeaseSpecification.isOpenEnded(filter.openEnded()));
        List<Lease> leases = leaseRepository.findAll(specification);
        return leaseMapper.toDtoList(leases);
    }

    @Override
    public LeaseResponseDto changeLeaseStatus(Long id, LeaseStatusRequestDto request) {
        Lease lease = findLeaseById(id);
        lease.setLeaseStatus(request.status());
        Lease savedLease = leaseRepository.save(lease);
        return leaseMapper.toDto(savedLease);
    }

    private Unit getAvailableUnit(CreateLeaseRequestDto request) {
        Unit unit = unitRepository.findById(request.unitId()).orElseThrow(
                () -> new EntityNotFoundException("Unit with id " + request.unitId()
                        + " not found in database"));
        if (!unit.isActive()) {
            throw new InactiveEntityException("Unit with id " + request.unitId() + " is inactive");
        }
        Optional<Lease> conflictingLease = leaseRepository.findConflictingLease(
                unit.getId(), LeaseStatus.ACTIVE, request.startDate());
        if (conflictingLease.isPresent()) {
            Lease lease = conflictingLease.get();
            String endDate;
            if (lease.getEndDate() == null) {
                endDate = " with no end date";
            } else {
                endDate = " till " + lease.getEndDate();
            }
            throw new EntityAlreadyExistsException("Unit with id " + unit.getId()
                    + " is already leased" + endDate);
        }
        return unit;
    }

    private Tenant getActiveTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tenant with id " + id
                        + " doesn't exist in database")
        );
        if (!tenant.isActive()) {
            throw new InactiveEntityException("Tenant with id " + id + " is inactive");
        }
        return tenant;
    }

    private void checkDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new InvalidLeaseDataException("Lease end date " + endDate
                    + " is before start date " + startDate);
        }
    }

    private Lease findLeaseById(Long id) {
        return leaseRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Lease with id " + id
                        + " doesn't exist in database")
        );
    }
}
