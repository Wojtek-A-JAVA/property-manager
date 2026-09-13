package property.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
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
import property.manager.model.VatRate;
import property.manager.repository.LeaseRepository;
import property.manager.repository.TenantRepository;
import property.manager.repository.UnitRepository;
import property.manager.service.impl.LeaseServiceImpl;

@ExtendWith(MockitoExtension.class)
class LeaseServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private LeaseMapper leaseMapper;

    private LeaseService leaseService;

    private Tenant tenant;
    private Unit unit;
    private Lease lease;

    @BeforeEach
    void setUp() {
        leaseService = new LeaseServiceImpl(
                tenantRepository,
                unitRepository,
                leaseRepository,
                leaseMapper
        );

        tenant = new Tenant();
        tenant.setId(10L);
        tenant.setActive(true);

        unit = new Unit();
        unit.setId(20L);
        unit.setActive(true);

        lease = new Lease();
        lease.setId(1L);
        lease.setTenant(tenant);
        lease.setUnit(unit);
        lease.setStartDate(LocalDate.of(2026, 1, 1));
        lease.setEndDate(LocalDate.of(2026, 12, 31));
        lease.setRentNetAmount(new BigDecimal("2500.00"));
        lease.setAdditionalCostsNetAmount(new BigDecimal("350.00"));
        lease.setDepositAmount(new BigDecimal("3000.00"));
        lease.setVatRate(VatRate.VAT_23);
        lease.setPaymentDueDay(10);
        lease.setNoticePeriodMonths(3);
        lease.setLeaseStatus(LeaseStatus.ACTIVE);
        lease.setNotes("Test lease");
    }

    @Test
    void createLeaseShouldSaveLease() {
        CreateLeaseRequestDto request = createRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                3,
                LeaseStatus.ACTIVE
        );

        LeaseResponseDto response = createResponse(LeaseStatus.ACTIVE);

        when(tenantRepository.findById(10L))
                .thenReturn(Optional.of(tenant));

        when(unitRepository.findById(20L))
                .thenReturn(Optional.of(unit));

        when(leaseRepository.findConflictingLease(
                20L,
                LeaseStatus.ACTIVE,
                request.startDate()))
                .thenReturn(Optional.empty());

        when(leaseMapper.toEntity(request))
                .thenReturn(lease);

        when(leaseRepository.save(lease))
                .thenReturn(lease);

        when(leaseMapper.toDto(lease))
                .thenReturn(response);

        LeaseResponseDto result = leaseService.createLease(request);

        assertThat(result).isEqualTo(response);
        assertThat(lease.getTenant()).isSameAs(tenant);
        assertThat(lease.getUnit()).isSameAs(unit);

        verify(leaseRepository).findConflictingLease(
                20L,
                LeaseStatus.ACTIVE,
                request.startDate());

        verify(leaseRepository).save(lease);
    }

    @Test
    void createLeaseShouldCreateOpenEndedLeaseWithNoticePeriod() {
        final CreateLeaseRequestDto request = createRequest(
                LocalDate.of(2026, 1, 1),
                null,
                3,
                LeaseStatus.ACTIVE
        );
        Lease mappedLease = new Lease();
        mappedLease.setLeaseStatus(LeaseStatus.ACTIVE);
        LeaseResponseDto response = new LeaseResponseDto(
                1L,
                10L,
                20L,
                LocalDate.of(2026, 1, 1),
                null,
                new BigDecimal("2500.00"),
                new BigDecimal("350.00"),
                new BigDecimal("3000.00"),
                VatRate.VAT_23,
                10,
                3,
                LeaseStatus.ACTIVE,
                "Test lease"
        );

        when(tenantRepository.findById(10L))
                .thenReturn(Optional.of(tenant));

        when(unitRepository.findById(20L))
                .thenReturn(Optional.of(unit));

        when(leaseRepository.findConflictingLease(
                20L,
                LeaseStatus.ACTIVE,
                request.startDate()))
                .thenReturn(Optional.empty());

        when(leaseMapper.toEntity(request))
                .thenReturn(mappedLease);

        when(leaseRepository.save(mappedLease))
                .thenReturn(mappedLease);

        when(leaseMapper.toDto(mappedLease))
                .thenReturn(response);

        LeaseResponseDto result = leaseService.createLease(request);

        assertThat(result.endDate()).isNull();
        assertThat(result.noticePeriodMonths()).isEqualTo(3);

        verify(leaseRepository).save(mappedLease);
    }

    @Test
    void createLeaseShouldThrowWhenOpenEndedLeaseHasNoNoticePeriod() {
        CreateLeaseRequestDto request = createRequest(
                LocalDate.of(2026, 1, 1),
                null,
                null,
                LeaseStatus.ACTIVE
        );

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(InvalidLeaseDataException.class)
                .hasMessage("Open-ended lease requires notice period");

        verifyNoInteractions(
                tenantRepository,
                unitRepository,
                leaseRepository,
                leaseMapper
        );
    }

    @Test
    void createLeaseShouldThrowWhenEndDateBeforeStartDate() {
        CreateLeaseRequestDto request = createRequest(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 5, 31),
                3,
                LeaseStatus.ACTIVE
        );

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(InvalidLeaseDataException.class)
                .hasMessage(
                        "Lease end date 2026-05-31"
                                + " is before start date 2026-06-01");

        verifyNoInteractions(
                tenantRepository,
                unitRepository,
                leaseRepository,
                leaseMapper
        );
    }

    @Test
    void createLeaseShouldThrowWhenTenantDoesNotExist() {
        CreateLeaseRequestDto request = createRequest();

        when(tenantRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        "Tenant with id 10 doesn't exist in database");

        verify(unitRepository, never()).findById(any());
        verify(leaseRepository, never()).save(any());
    }

    @Test
    void createLeaseShouldThrowWhenTenantInactive() {
        CreateLeaseRequestDto request = createRequest();

        tenant.setActive(false);

        when(tenantRepository.findById(10L))
                .thenReturn(Optional.of(tenant));

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(InactiveEntityException.class)
                .hasMessage("Tenant with id 10 is inactive");

        verify(unitRepository, never()).findById(any());
        verify(leaseRepository, never()).save(any());
    }

    @Test
    void createLeaseShouldThrowWhenUnitDoesNotExist() {
        CreateLeaseRequestDto request = createRequest();

        when(tenantRepository.findById(10L))
                .thenReturn(Optional.of(tenant));

        when(unitRepository.findById(20L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Unit with id 20 not found in database");

        verify(leaseRepository, never()).save(any());
    }

    @Test
    void createLeaseShouldThrowWhenUnitInactive() {
        final CreateLeaseRequestDto request = createRequest();
        unit.setActive(false);

        when(tenantRepository.findById(10L)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(20L)).thenReturn(Optional.of(unit));

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(InactiveEntityException.class)
                .hasMessage("Unit with id 20 is inactive");

        verify(leaseRepository, never()).findConflictingLease(
                any(),
                any(),
                any());

        verify(leaseRepository, never()).save(any());
    }

    @Test
    void createLeaseShouldThrowWhenUnitHasConflictingDatedLease() {
        final CreateLeaseRequestDto request = createRequest();

        Lease conflictingLease = new Lease();
        conflictingLease.setId(2L);
        conflictingLease.setEndDate(LocalDate.of(2026, 12, 31));
        conflictingLease.setLeaseStatus(LeaseStatus.ACTIVE);

        when(tenantRepository.findById(10L)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(20L)).thenReturn(Optional.of(unit));
        when(leaseRepository.findConflictingLease(
                20L,
                LeaseStatus.ACTIVE,
                request.startDate()))
                .thenReturn(Optional.of(conflictingLease));

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage(
                        "Unit with id 20 is already leased till 2026-12-31");

        verify(leaseRepository, never()).save(any());
        verify(leaseMapper, never()).toEntity(any());
    }

    @Test
    void createLeaseShouldThrowWhenUnitHasConflictingOpenEndedLease() {
        final CreateLeaseRequestDto request = createRequest();

        Lease conflictingLease = new Lease();
        conflictingLease.setId(2L);
        conflictingLease.setEndDate(null);
        conflictingLease.setLeaseStatus(LeaseStatus.ACTIVE);

        when(tenantRepository.findById(10L))
                .thenReturn(Optional.of(tenant));

        when(unitRepository.findById(20L))
                .thenReturn(Optional.of(unit));

        when(leaseRepository.findConflictingLease(
                20L,
                LeaseStatus.ACTIVE,
                request.startDate()))
                .thenReturn(Optional.of(conflictingLease));

        assertThatThrownBy(() -> leaseService.createLease(request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage(
                        "Unit with id 20 is already leased"
                                + " with no end date");

        verify(leaseRepository, never()).save(any());
        verify(leaseMapper, never()).toEntity(any());
    }

    @Test
    void createLeaseShouldSetActiveStatusWhenMappedStatusIsNull() {
        final CreateLeaseRequestDto request = createRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                3,
                null
        );
        Lease mappedLease = new Lease();
        mappedLease.setLeaseStatus(null);
        when(tenantRepository.findById(10L)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(20L)).thenReturn(Optional.of(unit));
        when(leaseRepository.findConflictingLease(
                20L,
                LeaseStatus.ACTIVE,
                request.startDate()))
                .thenReturn(Optional.empty());
        when(leaseMapper.toEntity(request)).thenReturn(mappedLease);
        when(leaseRepository.save(mappedLease)).thenReturn(mappedLease);
        when(leaseMapper.toDto(mappedLease)).thenReturn(createResponse(LeaseStatus.ACTIVE));
        leaseService.createLease(request);

        assertThat(mappedLease.getLeaseStatus())
                .isEqualTo(LeaseStatus.ACTIVE);

        verify(leaseRepository).save(mappedLease);
    }

    @Test
    void createLeaseShouldKeepPlannedStatus() {
        final CreateLeaseRequestDto request = createRequest(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 9, 30),
                3,
                LeaseStatus.PLANNED
        );
        Lease mappedLease = new Lease();
        mappedLease.setLeaseStatus(LeaseStatus.PLANNED);
        when(tenantRepository.findById(10L)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(20L)).thenReturn(Optional.of(unit));
        when(leaseRepository.findConflictingLease(
                20L,
                LeaseStatus.ACTIVE,
                request.startDate()))
                .thenReturn(Optional.empty());
        when(leaseMapper.toEntity(request)).thenReturn(mappedLease);
        when(leaseRepository.save(mappedLease)).thenReturn(mappedLease);
        when(leaseMapper.toDto(mappedLease)).thenReturn(createResponse(LeaseStatus.PLANNED));
        leaseService.createLease(request);

        assertThat(mappedLease.getLeaseStatus())
                .isEqualTo(LeaseStatus.PLANNED);

        verify(leaseRepository).save(mappedLease);
    }

    @Test
    void getLeaseShouldReturnLease() {
        LeaseResponseDto response =
                createResponse(LeaseStatus.ACTIVE);

        when(leaseRepository.findById(1L))
                .thenReturn(Optional.of(lease));

        when(leaseMapper.toDto(lease))
                .thenReturn(response);

        LeaseResponseDto result = leaseService.getLease(1L);

        assertThat(result).isEqualTo(response);

        verify(leaseRepository).findById(1L);
        verify(leaseMapper).toDto(lease);
    }

    @Test
    void getLeaseShouldThrowWhenLeaseDoesNotExist() {
        when(leaseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaseService.getLease(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        "Lease with id 999 doesn't exist in database");

        verify(leaseMapper, never()).toDto(any());
    }

    @Test
    void getLeasesShouldReturnMappedLeases() {
        LeaseFilterDto filter = new LeaseFilterDto(
                LeaseStatus.ACTIVE,
                10L,
                20L,
                VatRate.VAT_23,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                false
        );

        List<LeaseResponseDto> responses =
                List.of(createResponse(LeaseStatus.ACTIVE));

        when(leaseRepository.findAll(
                ArgumentMatchers.<Specification<Lease>>any()))
                .thenReturn(List.of(lease));

        when(leaseMapper.toDtoList(List.of(lease)))
                .thenReturn(responses);

        List<LeaseResponseDto> result =
                leaseService.getLeases(filter);

        assertThat(result).isEqualTo(responses);
        assertThat(result).hasSize(1);

        verify(leaseRepository).findAll(
                ArgumentMatchers.<Specification<Lease>>any());

        verify(leaseMapper).toDtoList(List.of(lease));
    }

    @Test
    void getLeasesShouldThrowWhenEndDateBeforeStartDate() {
        LeaseFilterDto filter = new LeaseFilterDto(
                null,
                null,
                null,
                null,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 5, 31),
                false
        );

        assertThatThrownBy(() -> leaseService.getLeases(filter))
                .isInstanceOf(InvalidLeaseDataException.class)
                .hasMessage(
                        "Lease end date 2026-05-31"
                                + " is before start date 2026-06-01");

        verify(leaseRepository, never()).findAll(
                ArgumentMatchers.<Specification<Lease>>any());

        verify(leaseMapper, never()).toDtoList(any());
    }

    @Test
    void changeLeaseStatusShouldSaveNewStatus() {
        LeaseStatusRequestDto request =
                new LeaseStatusRequestDto(LeaseStatus.ENDED);

        LeaseResponseDto response =
                createResponse(LeaseStatus.ENDED);

        when(leaseRepository.findById(1L))
                .thenReturn(Optional.of(lease));

        when(leaseRepository.save(lease))
                .thenReturn(lease);

        when(leaseMapper.toDto(lease))
                .thenReturn(response);

        LeaseResponseDto result =
                leaseService.changeLeaseStatus(1L, request);

        assertThat(lease.getLeaseStatus())
                .isEqualTo(LeaseStatus.ENDED);

        assertThat(result.leaseStatus())
                .isEqualTo(LeaseStatus.ENDED);

        verify(leaseRepository).save(lease);
        verify(leaseMapper).toDto(lease);
    }

    @Test
    void changeLeaseStatusShouldThrowWhenLeaseDoesNotExist() {
        LeaseStatusRequestDto request =
                new LeaseStatusRequestDto(LeaseStatus.ENDED);

        when(leaseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> leaseService.changeLeaseStatus(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        "Lease with id 999 doesn't exist in database");

        verify(leaseRepository, never()).save(any());
        verify(leaseMapper, never()).toDto(any());
    }

    private CreateLeaseRequestDto createRequest() {
        return createRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                3,
                LeaseStatus.ACTIVE
        );
    }

    private CreateLeaseRequestDto createRequest(
            LocalDate startDate,
            LocalDate endDate,
            Integer noticePeriodMonths,
            LeaseStatus leaseStatus) {

        return new CreateLeaseRequestDto(
                10L,
                20L,
                startDate,
                endDate,
                new BigDecimal("2500.00"),
                new BigDecimal("350.00"),
                new BigDecimal("3000.00"),
                VatRate.VAT_23,
                10,
                noticePeriodMonths,
                leaseStatus,
                "Test lease"
        );
    }

    private LeaseResponseDto createResponse(
            LeaseStatus leaseStatus) {

        return new LeaseResponseDto(
                1L,
                10L,
                20L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("2500.00"),
                new BigDecimal("350.00"),
                new BigDecimal("3000.00"),
                VatRate.VAT_23,
                10,
                3,
                leaseStatus,
                "Test lease"
        );
    }
}
