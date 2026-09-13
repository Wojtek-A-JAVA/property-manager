package property.manager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import property.manager.model.MeasurementType;
import property.manager.model.Meter;
import property.manager.model.MeterPurpose;
import property.manager.model.Property;
import property.manager.model.Unit;
import property.manager.model.UnitType;

@DataJpaTest(
        properties = {
                "spring.liquibase.enabled=true",
                "spring.liquibase.contexts=test",
                "spring.jpa.hibernate.ddl-auto=validate"
        })
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeterRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRESQL =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UnitRepository unitRepository;

    private Property property;
    private Unit unit;

    @BeforeEach
    void setUp() {
        property = new Property();
        property.setName("Test Property");
        property.setAddress("Test Street 1");
        property.setDescription("Property used in repository tests");
        property.setActive(true);
        property = propertyRepository.saveAndFlush(property);

        unit = new Unit();
        unit.setProperty(property);
        unit.setName("Apartment 1");
        unit.setUnitNumber("1");
        unit.setType(UnitType.APARTMENT);
        unit.setActive(true);
        unit = unitRepository.saveAndFlush(unit);
    }

    @Test
    void shouldSaveAndFindMeterById() {
        Meter meter = createPropertyMeter(true);

        Meter savedMeter = meterRepository.saveAndFlush(meter);

        Optional<Meter> result = meterRepository.findById(savedMeter.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedMeter.getId());
        assertThat(result.get().getName()).isEqualTo("Main water meter");
        assertThat(result.get().getProperty().getId()).isEqualTo(property.getId());
        assertThat(result.get().getUnit()).isNull();
        assertThat(result.get().getMeasurementType())
                .isEqualTo(MeasurementType.WATER);
        assertThat(result.get().getPurpose())
                .isEqualTo(MeterPurpose.COLD_WATER);
        assertThat(result.get().isActive()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenMeterDoesNotExist() {
        Optional<Meter> result = meterRepository.findById(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteMeter() {
        Meter meter = meterRepository.saveAndFlush(createPropertyMeter(true));
        Long meterId = meter.getId();

        meterRepository.delete(meter);
        meterRepository.flush();

        assertThat(meterRepository.findById(meterId)).isEmpty();
    }

    @Test
    void shouldDetectActivePropertyLevelDuplicate() {
        meterRepository.saveAndFlush(createPropertyMeter(true));

        boolean exists = meterRepository
                .existsByPropertyIdAndUnitIsNullAndMeasurementTypeAndPurposeAndActiveTrue(
                        property.getId(),
                        MeasurementType.WATER,
                        MeterPurpose.COLD_WATER);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotDetectInactivePropertyLevelMeterAsDuplicate() {
        meterRepository.saveAndFlush(createPropertyMeter(false));

        boolean exists = meterRepository
                .existsByPropertyIdAndUnitIsNullAndMeasurementTypeAndPurposeAndActiveTrue(
                        property.getId(),
                        MeasurementType.WATER,
                        MeterPurpose.COLD_WATER);

        assertThat(exists).isFalse();
    }

    @Test
    void shouldDetectActiveUnitLevelDuplicate() {
        meterRepository.saveAndFlush(createUnitMeter(true));

        boolean exists =
                meterRepository.existsByUnitIdAndMeasurementTypeAndPurposeAndActiveTrue(
                        unit.getId(),
                        MeasurementType.WATER,
                        MeterPurpose.COLD_WATER);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotDetectInactiveUnitLevelMeterAsDuplicate() {
        meterRepository.saveAndFlush(createUnitMeter(false));

        boolean exists =
                meterRepository.existsByUnitIdAndMeasurementTypeAndPurposeAndActiveTrue(
                        unit.getId(),
                        MeasurementType.WATER,
                        MeterPurpose.COLD_WATER);

        assertThat(exists).isFalse();
    }

    @Test
    void propertyLevelQueryShouldIgnoreUnitLevelMeter() {
        meterRepository.saveAndFlush(createUnitMeter(true));

        boolean exists = meterRepository
                .existsByPropertyIdAndUnitIsNullAndMeasurementTypeAndPurposeAndActiveTrue(
                        property.getId(),
                        MeasurementType.WATER,
                        MeterPurpose.COLD_WATER);

        assertThat(exists).isFalse();
    }

    @Test
    void unitLevelQueryShouldNotMatchDifferentMeasurementType() {
        meterRepository.saveAndFlush(createUnitMeter(true));

        boolean exists =
                meterRepository.existsByUnitIdAndMeasurementTypeAndPurposeAndActiveTrue(
                        unit.getId(),
                        MeasurementType.ELECTRICITY,
                        MeterPurpose.COLD_WATER);

        assertThat(exists).isFalse();
    }

    @Test
    void unitLevelQueryShouldNotMatchDifferentPurpose() {
        meterRepository.saveAndFlush(createUnitMeter(true));

        boolean exists =
                meterRepository.existsByUnitIdAndMeasurementTypeAndPurposeAndActiveTrue(
                        unit.getId(),
                        MeasurementType.WATER,
                        MeterPurpose.HOT_WATER);

        assertThat(exists).isFalse();
    }

    private Meter createPropertyMeter(boolean active) {
        Meter meter = new Meter();
        meter.setProperty(property);
        meter.setUnit(null);
        meter.setMeasurementType(MeasurementType.WATER);
        meter.setPurpose(MeterPurpose.COLD_WATER);
        meter.setName("Main water meter");
        meter.setNotes("Repository test");
        meter.setActive(active);
        return meter;
    }

    private Meter createUnitMeter(boolean active) {
        Meter meter = new Meter();
        meter.setProperty(property);
        meter.setUnit(unit);
        meter.setMeasurementType(MeasurementType.WATER);
        meter.setPurpose(MeterPurpose.COLD_WATER);
        meter.setName("Apartment water meter");
        meter.setNotes("Repository test");
        meter.setActive(active);
        return meter;
    }
}
