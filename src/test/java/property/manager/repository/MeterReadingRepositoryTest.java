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
import property.manager.model.MeasurementType;
import property.manager.model.Meter;
import property.manager.model.MeterPurpose;
import property.manager.model.MeterReading;
import property.manager.model.Property;
import property.manager.specification.MeterReadingSpecification;

@DataJpaTest(
        properties = {
                "spring.liquibase.enabled=true",
                "spring.liquibase.contexts=test",
                "spring.jpa.hibernate.ddl-auto=validate"
        })
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeterReadingRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRESQL =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    private Meter firstMeter;
    private Meter secondMeter;

    @BeforeEach
    void setUp() {
        Property property = new Property();
        property.setName("Test Property");
        property.setAddress("Test Street 1");
        property.setActive(true);
        property = propertyRepository.saveAndFlush(property);

        firstMeter = new Meter();
        firstMeter.setProperty(property);
        firstMeter.setMeasurementType(MeasurementType.WATER);
        firstMeter.setPurpose(MeterPurpose.COLD_WATER);
        firstMeter.setName("Water meter");
        firstMeter.setActive(true);
        firstMeter = meterRepository.saveAndFlush(firstMeter);

        secondMeter = new Meter();
        secondMeter.setProperty(property);
        secondMeter.setMeasurementType(MeasurementType.ELECTRICITY);
        secondMeter.setPurpose(MeterPurpose.GENERAL);
        secondMeter.setName("Electricity meter");
        secondMeter.setActive(true);
        secondMeter = meterRepository.saveAndFlush(secondMeter);
    }

    @Test
    void shouldSaveAndFindReadingById() {
        MeterReading reading = createReading(
                firstMeter,
                LocalDate.of(2026, 1, 10),
                "15.321");

        MeterReading savedReading =
                meterReadingRepository.saveAndFlush(reading);

        Optional<MeterReading> result =
                meterReadingRepository.findById(savedReading.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId())
                .isEqualTo(savedReading.getId());
        assertThat(result.get().getMeter().getId())
                .isEqualTo(firstMeter.getId());
        assertThat(result.get().getReadingDate())
                .isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(result.get().getValue())
                .isEqualByComparingTo("15.321");
        assertThat(result.get().getNotes())
                .isEqualTo("Repository test reading");
    }

    @Test
    void shouldReturnEmptyWhenReadingDoesNotExist() {
        Optional<MeterReading> result =
                meterReadingRepository.findById(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDetectReadingForSameMeterAndDate() {
        LocalDate readingDate = LocalDate.of(2026, 2, 10);

        meterReadingRepository.saveAndFlush(
                createReading(firstMeter, readingDate, "20.500"));

        boolean exists =
                meterReadingRepository.existsByMeterIdAndReadingDate(
                        firstMeter.getId(),
                        readingDate);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotDetectReadingForDifferentDate() {
        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 2, 10),
                        "20.500"));

        boolean exists =
                meterReadingRepository.existsByMeterIdAndReadingDate(
                        firstMeter.getId(),
                        LocalDate.of(2026, 2, 11));

        assertThat(exists).isFalse();
    }

    @Test
    void shouldNotDetectReadingForDifferentMeter() {
        LocalDate readingDate = LocalDate.of(2026, 2, 10);

        meterReadingRepository.saveAndFlush(
                createReading(firstMeter, readingDate, "20.500"));

        boolean exists =
                meterReadingRepository.existsByMeterIdAndReadingDate(
                        secondMeter.getId(),
                        readingDate);

        assertThat(exists).isFalse();
    }

    @Test
    void shouldFilterReadingsByMeterId() {
        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 1, 1),
                        "10.000"));

        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 2, 1),
                        "15.000"));

        meterReadingRepository.saveAndFlush(
                createReading(
                        secondMeter,
                        LocalDate.of(2026, 1, 1),
                        "100.000"));

        Specification<MeterReading> specification =
                MeterReadingSpecification.hasMeterId(
                        firstMeter.getId());

        List<MeterReading> result =
                meterReadingRepository.findAll(specification);

        assertThat(result).hasSize(2);
        assertThat(result)
                .allMatch(reading ->
                        reading.getMeter().getId()
                                .equals(firstMeter.getId()));
    }

    @Test
    void shouldFilterReadingsFromDate() {
        saveReadingsForPeriod();

        Specification<MeterReading> specification =
                MeterReadingSpecification.isWithinPeriod(
                        LocalDate.of(2026, 2, 1),
                        null);

        List<MeterReading> result =
                meterReadingRepository.findAll(specification);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(MeterReading::getReadingDate)
                .containsExactlyInAnyOrder(
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 3, 1));
    }

    @Test
    void shouldFilterReadingsToDate() {
        saveReadingsForPeriod();

        Specification<MeterReading> specification =
                MeterReadingSpecification.isWithinPeriod(
                        null,
                        LocalDate.of(2026, 2, 1));

        List<MeterReading> result =
                meterReadingRepository.findAll(specification);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(MeterReading::getReadingDate)
                .containsExactlyInAnyOrder(
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 2, 1));
    }

    @Test
    void shouldFilterReadingsWithinPeriod() {
        saveReadingsForPeriod();

        Specification<MeterReading> specification =
                MeterReadingSpecification.isWithinPeriod(
                        LocalDate.of(2026, 1, 15),
                        LocalDate.of(2026, 2, 15));

        List<MeterReading> result =
                meterReadingRepository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getReadingDate())
                .isEqualTo(LocalDate.of(2026, 2, 1));
    }

    @Test
    void shouldReturnAllReadingsWhenSpecificationValuesAreNull() {
        saveReadingsForPeriod();

        Specification<MeterReading> specification =
                MeterReadingSpecification
                        .hasMeterId(null)
                        .and(MeterReadingSpecification
                                .isWithinPeriod(null, null));

        List<MeterReading> result =
                meterReadingRepository.findAll(specification);

        assertThat(result).hasSize(3);
    }

    @Test
    void shouldFilterByMeterIdAndDateRange() {
        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 1, 1),
                        "10.000"));

        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 2, 1),
                        "15.000"));

        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 3, 1),
                        "20.000"));

        meterReadingRepository.saveAndFlush(
                createReading(
                        secondMeter,
                        LocalDate.of(2026, 2, 1),
                        "200.000"));

        Specification<MeterReading> specification =
                MeterReadingSpecification
                        .hasMeterId(firstMeter.getId())
                        .and(MeterReadingSpecification
                                .isWithinPeriod(
                                        LocalDate.of(2026, 1, 15),
                                        LocalDate.of(2026, 2, 15)));

        List<MeterReading> result =
                meterReadingRepository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMeter().getId())
                .isEqualTo(firstMeter.getId());
        assertThat(result.getFirst().getReadingDate())
                .isEqualTo(LocalDate.of(2026, 2, 1));
    }

    private MeterReading createReading(
            Meter meter,
            LocalDate readingDate,
            String value) {

        MeterReading reading = new MeterReading();
        reading.setMeter(meter);
        reading.setReadingDate(readingDate);
        reading.setValue(new BigDecimal(value));
        reading.setNotes("Repository test reading");

        return reading;
    }

    private void saveReadingsForPeriod() {
        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 1, 1),
                        "10.000"));

        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 2, 1),
                        "15.000"));

        meterReadingRepository.saveAndFlush(
                createReading(
                        firstMeter,
                        LocalDate.of(2026, 3, 1),
                        "20.000"));
    }
}
