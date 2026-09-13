package property.manager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import property.manager.model.Lease;
import property.manager.model.LeaseStatus;
import property.manager.model.Property;
import property.manager.model.Tenant;
import property.manager.model.TenantType;
import property.manager.model.Unit;
import property.manager.model.UnitType;
import property.manager.model.VatRate;
import property.manager.specification.LeaseSpecification;

@DataJpaTest(properties = {
        "spring.liquibase.enabled=true",
        "spring.liquibase.contexts=test",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE)
class LeaseRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRESQL =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant tenant;
    private Tenant secondTenant;
    private Unit unit;
    private Unit secondUnit;

    @BeforeEach
    void setUp() {
        Property property = new Property();
        property.setName("Test Property");
        property.setAddress("Test Street 1");
        property.setActive(true);
        property = propertyRepository.saveAndFlush(property);

        unit = createUnit(
                property,
                "Apartment 1",
                "1",
                UnitType.APARTMENT);

        secondUnit = createUnit(
                property,
                "Apartment 2",
                "2",
                UnitType.APARTMENT);

        tenant = createTenant(
                "Jan",
                "Kowalski",
                "90010112345",
                "jan.kowalski@test.pl");

        secondTenant = createTenant(
                "Anna",
                "Nowak",
                "91020212345",
                "anna.nowak@test.pl");
    }

    @Test
    void shouldSaveAndFindLeaseById() {
        Lease lease = createLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Lease saved = leaseRepository.saveAndFlush(lease);

        Optional<Lease> result =
                leaseRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTenant().getId())
                .isEqualTo(tenant.getId());
        assertThat(result.get().getUnit().getId())
                .isEqualTo(unit.getId());
        assertThat(result.get().getLeaseStatus())
                .isEqualTo(LeaseStatus.ACTIVE);
    }

    @Test
    void shouldReturnEmptyWhenLeaseDoesNotExist() {
        Optional<Lease> result =
                leaseRepository.findById(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindConflictWhenActiveLeaseEndsAfterNewStartDate() {
        createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Optional<Lease> result =
                leaseRepository.findConflictingLease(
                        unit.getId(),
                        LeaseStatus.ACTIVE,
                        LocalDate.of(2026, 6, 1));

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindConflictWhenActiveLeaseEndsOnNewStartDate() {
        createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Optional<Lease> result =
                leaseRepository.findConflictingLease(
                        unit.getId(),
                        LeaseStatus.ACTIVE,
                        LocalDate.of(2026, 6, 1));

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindConflictWhenActiveLeaseIsOpenEnded() {
        createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                null,
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Optional<Lease> result =
                leaseRepository.findConflictingLease(
                        unit.getId(),
                        LeaseStatus.ACTIVE,
                        LocalDate.of(2027, 1, 1));

        assertThat(result).isPresent();
    }

    @Test
    void shouldNotFindConflictWhenPreviousLeaseEndedBeforeNewStartDate() {
        createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 5, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Optional<Lease> result =
                leaseRepository.findConflictingLease(
                        unit.getId(),
                        LeaseStatus.ACTIVE,
                        LocalDate.of(2026, 6, 1));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotFindConflictForDifferentUnit() {
        createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                null,
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Optional<Lease> result =
                leaseRepository.findConflictingLease(
                        secondUnit.getId(),
                        LeaseStatus.ACTIVE,
                        LocalDate.of(2026, 6, 1));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotFindConflictWhenLeaseStatusDoesNotMatch() {
        createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                null,
                LeaseStatus.ENDED,
                VatRate.VAT_23);

        Optional<Lease> result =
                leaseRepository.findConflictingLease(
                        unit.getId(),
                        LeaseStatus.ACTIVE,
                        LocalDate.of(2026, 6, 1));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterByStatus() {
        Lease activeLease = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                LeaseStatus.ENDED,
                VatRate.NONE);

        Specification<Lease> specification =
                LeaseSpecification.hasStatus(LeaseStatus.ACTIVE);

        List<Lease> result =
                leaseRepository.findAll(specification);

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(activeLease.getId());
    }

    @Test
    void shouldFilterByTenantId() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        List<Lease> result = leaseRepository.findAll(
                LeaseSpecification.hasTenantId(tenant.getId()));

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldFilterByUnitId() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        List<Lease> result = leaseRepository.findAll(
                LeaseSpecification.hasUnitId(unit.getId()));

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldFilterByVatRate() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                LeaseStatus.ACTIVE,
                VatRate.NONE);

        List<Lease> result = leaseRepository.findAll(
                LeaseSpecification.hasVatRate(VatRate.VAT_23));

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldFilterOpenEndedLeases() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                null,
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        List<Lease> result = leaseRepository.findAll(
                LeaseSpecification.isOpenEnded(true));

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldFilterClosedEndedLeases() {
        createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                null,
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Lease expected = createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        List<Lease> result = leaseRepository.findAll(
                LeaseSpecification.isOpenEnded(false));

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldFindLeaseOverlappingPeriod() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 9, 30),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                LeaseStatus.ENDED,
                VatRate.NONE);

        Specification<Lease> specification =
                LeaseSpecification.overlapsPeriod(
                        LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 6, 30));

        List<Lease> result =
                leaseRepository.findAll(specification);

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldIncludeOpenEndedLeaseWhenFilteringFromDate() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                null,
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                LeaseStatus.ENDED,
                VatRate.NONE);

        List<Lease> result = leaseRepository.findAll(
                LeaseSpecification.overlapsPeriod(
                        LocalDate.of(2027, 1, 1),
                        null));

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldFilterByToDate() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 12, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 12, 31),
                LeaseStatus.PLANNED,
                VatRate.NONE);

        List<Lease> result = leaseRepository.findAll(
                LeaseSpecification.overlapsPeriod(
                        null,
                        LocalDate.of(2026, 6, 30)));

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void shouldReturnAllWhenSpecificationValuesAreNull() {
        Lease firstLease = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        Lease secondLease = createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2027, 1, 1),
                null,
                LeaseStatus.PLANNED,
                VatRate.NONE);

        Specification<Lease> specification =
                LeaseSpecification.hasStatus(null)
                        .and(LeaseSpecification.hasTenantId(null))
                        .and(LeaseSpecification.hasUnitId(null))
                        .and(LeaseSpecification.hasVatRate(null))
                        .and(LeaseSpecification.overlapsPeriod(
                                null,
                                null))
                        .and(LeaseSpecification.isOpenEnded(null));

        List<Lease> result =
                leaseRepository.findAll(specification);

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactlyInAnyOrder(
                        firstLease.getId(),
                        secondLease.getId());
    }

    @Test
    void shouldFilterByCombinedCriteria() {
        Lease expected = createAndSaveLease(
                tenant,
                unit,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LeaseStatus.ACTIVE,
                VatRate.VAT_23);

        createAndSaveLease(
                secondTenant,
                secondUnit,
                LocalDate.of(2026, 1, 1),
                null,
                LeaseStatus.ACTIVE,
                VatRate.NONE);

        Specification<Lease> specification =
                LeaseSpecification.hasStatus(LeaseStatus.ACTIVE)
                        .and(LeaseSpecification.hasTenantId(
                                tenant.getId()))
                        .and(LeaseSpecification.hasUnitId(
                                unit.getId()))
                        .and(LeaseSpecification.hasVatRate(
                                VatRate.VAT_23))
                        .and(LeaseSpecification.overlapsPeriod(
                                LocalDate.of(2026, 6, 1),
                                LocalDate.of(2026, 6, 30)))
                        .and(LeaseSpecification.isOpenEnded(false));

        List<Lease> result =
                leaseRepository.findAll(specification);

        assertThat(result)
                .extracting(Lease::getId)
                .containsExactly(expected.getId());
    }

    private Lease createAndSaveLease(
            Tenant tenant,
            Unit unit,
            LocalDate startDate,
            LocalDate endDate,
            LeaseStatus status,
            VatRate vatRate) {

        return leaseRepository.saveAndFlush(
                createLease(
                        tenant,
                        unit,
                        startDate,
                        endDate,
                        status,
                        vatRate));
    }

    private Lease createLease(
            Tenant tenant,
            Unit unit,
            LocalDate startDate,
            LocalDate endDate,
            LeaseStatus status,
            VatRate vatRate) {

        Lease lease = new Lease();
        lease.setTenant(tenant);
        lease.setUnit(unit);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setRentNetAmount(new BigDecimal("2500.00"));
        lease.setAdditionalCostsNetAmount(
                new BigDecimal("350.00"));
        lease.setDepositAmount(new BigDecimal("3000.00"));
        lease.setVatRate(vatRate);
        lease.setPaymentDueDay(10);
        lease.setNoticePeriodMonths(3);
        lease.setLeaseStatus(status);
        lease.setNotes("Repository test");

        return lease;
    }

    private Unit createUnit(
            Property property,
            String name,
            String unitNumber,
            UnitType type) {

        Unit unit = new Unit();
        unit.setProperty(property);
        unit.setName(name);
        unit.setUnitNumber(unitNumber);
        unit.setType(type);
        unit.setArea(new BigDecimal("50.00"));
        unit.setActive(true);

        return unitRepository.saveAndFlush(unit);
    }

    private Tenant createTenant(
            String firstName,
            String lastName,
            String pesel,
            String email) {

        Tenant tenant = new Tenant();
        tenant.setType(TenantType.INDIVIDUAL);
        tenant.setFirstName(firstName);
        tenant.setLastName(lastName);
        tenant.setPesel(pesel);
        tenant.setContactFirstName(firstName);
        tenant.setContactLastName(lastName);
        tenant.setAddress("Test address");
        tenant.setEmail(email);
        tenant.setPhone("500100100");
        tenant.setActive(true);

        return tenantRepository.saveAndFlush(tenant);
    }
}
