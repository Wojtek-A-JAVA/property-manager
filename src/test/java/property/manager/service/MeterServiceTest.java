package property.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import property.manager.dto.meter.CreateMeterReadingRequestDto;
import property.manager.dto.meter.CreateMeterRequestDto;
import property.manager.dto.meter.MeterFilterDto;
import property.manager.dto.meter.MeterReadingFilterDto;
import property.manager.dto.meter.MeterReadingResponseDto;
import property.manager.dto.meter.MeterResponseDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InactiveEntityException;
import property.manager.exception.InvalidMeterDataException;
import property.manager.mapper.MeterMapper;
import property.manager.mapper.MeterReadingMapper;
import property.manager.model.MeasurementType;
import property.manager.model.Meter;
import property.manager.model.MeterPurpose;
import property.manager.model.MeterReading;
import property.manager.model.Property;
import property.manager.model.Unit;
import property.manager.repository.MeterReadingRepository;
import property.manager.repository.MeterRepository;
import property.manager.repository.PropertyRepository;
import property.manager.repository.UnitRepository;
import property.manager.service.impl.MeterServiceImpl;

@ExtendWith(MockitoExtension.class)
class MeterServiceTest {

    @Mock
    private MeterRepository meterRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private MeterMapper meterMapper;

    @Mock
    private MeterReadingRepository meterReadingRepository;

    @Mock
    private MeterReadingMapper meterReadingMapper;

    private MeterService meterService;
    private Property property;
    private Unit unit;
    private Meter meter;

    @BeforeEach
    void setUp() {
        meterService = new MeterServiceImpl(
                meterRepository,
                propertyRepository,
                unitRepository,
                meterMapper,
                meterReadingRepository,
                meterReadingMapper
        );
        property = new Property();
        property.setId(1L);
        property.setName("Test Property");
        property.setAddress("Test Street 1");
        property.setActive(true);

        unit = new Unit();
        unit.setId(10L);
        unit.setProperty(property);
        unit.setName("Apartment 1");
        unit.setUnitNumber("1");
        unit.setActive(true);

        meter = new Meter();
        meter.setId(100L);
        meter.setProperty(property);
        meter.setUnit(unit);
        meter.setMeasurementType(MeasurementType.WATER);
        meter.setPurpose(MeterPurpose.COLD_WATER);
        meter.setName("Water meter");
        meter.setActive(true);
    }

    @Test
    void createShouldSavePropertyLevelMeter() {
        CreateMeterRequestDto request = new CreateMeterRequestDto(
                1L,
                null,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Main water meter",
                "Test meter");

        Meter meterToSave = new Meter();

        MeterResponseDto expectedResponse = new MeterResponseDto(
                100L,
                1L,
                null,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Main water meter",
                "Test meter",
                true);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        when(meterMapper.toEntity(request))
                .thenReturn(meterToSave);

        when(meterRepository.save(meterToSave))
                .thenReturn(meterToSave);

        when(meterMapper.toDto(meterToSave))
                .thenReturn(expectedResponse);

        MeterResponseDto result = meterService.create(request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(meterToSave.getProperty()).isEqualTo(property);
        assertThat(meterToSave.getUnit()).isNull();

        verify(meterRepository).save(meterToSave);
    }

    @Test
    void createShouldSaveUnitLevelMeter() {
        CreateMeterRequestDto request = new CreateMeterRequestDto(
                1L,
                10L,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Apartment water meter",
                null);

        Meter meterToSave = new Meter();

        MeterResponseDto expectedResponse = new MeterResponseDto(
                100L,
                1L,
                10L,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Apartment water meter",
                null,
                true);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        when(unitRepository.findById(10L))
                .thenReturn(Optional.of(unit));

        when(meterMapper.toEntity(request))
                .thenReturn(meterToSave);

        when(meterRepository.save(meterToSave))
                .thenReturn(meterToSave);

        when(meterMapper.toDto(meterToSave))
                .thenReturn(expectedResponse);

        MeterResponseDto result = meterService.create(request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(meterToSave.getProperty()).isEqualTo(property);
        assertThat(meterToSave.getUnit()).isEqualTo(unit);

        verify(meterRepository).save(meterToSave);
    }

    @Test
    void createShouldThrowWhenPropertyDoesNotExist() {
        CreateMeterRequestDto request = createMeterRequest(null);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> meterService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Property with id 1");

        verify(meterRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenPropertyIsInactive() {
        property.setActive(false);

        CreateMeterRequestDto request = createMeterRequest(null);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        assertThatThrownBy(() -> meterService.create(request))
                .isInstanceOf(InactiveEntityException.class)
                .hasMessageContaining("Property with id 1 is inactive");

        verify(meterRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenUnitDoesNotExist() {
        CreateMeterRequestDto request = createMeterRequest(10L);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        when(unitRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> meterService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Unit with id 10");

        verify(meterRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenUnitBelongsToDifferentProperty() {
        Property otherProperty = new Property();
        otherProperty.setId(2L);
        otherProperty.setActive(true);

        unit.setProperty(otherProperty);

        CreateMeterRequestDto request = createMeterRequest(10L);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        when(unitRepository.findById(10L))
                .thenReturn(Optional.of(unit));

        assertThatThrownBy(() -> meterService.create(request))
                .isInstanceOf(InvalidMeterDataException.class)
                .hasMessageContaining(
                        "Unit with id 10 is not in property with id 1");

        verify(meterRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenUnitIsInactive() {
        unit.setActive(false);

        CreateMeterRequestDto request = createMeterRequest(10L);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        when(unitRepository.findById(10L))
                .thenReturn(Optional.of(unit));

        assertThatThrownBy(() -> meterService.create(request))
                .isInstanceOf(InactiveEntityException.class)
                .hasMessageContaining("Unit with id 10 is inactive");

        verify(meterRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenPropertyLevelMeterAlreadyExists() {
        CreateMeterRequestDto request = createMeterRequest(null);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        when(meterRepository
                .existsByPropertyIdAndUnitIsNullAndMeasurementTypeAndPurposeAndActiveTrue(
                        1L,
                        MeasurementType.WATER,
                        MeterPurpose.COLD_WATER))
                .thenReturn(true);

        assertThatThrownBy(() -> meterService.create(request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Meter in property with id 1");

        verify(meterMapper, never()).toEntity(any());
        verify(meterRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenUnitLevelMeterAlreadyExists() {
        CreateMeterRequestDto request = createMeterRequest(10L);

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        when(unitRepository.findById(10L))
                .thenReturn(Optional.of(unit));

        when(meterRepository
                .existsByUnitIdAndMeasurementTypeAndPurposeAndActiveTrue(
                        10L,
                        MeasurementType.WATER,
                        MeterPurpose.COLD_WATER))
                .thenReturn(true);

        assertThatThrownBy(() -> meterService.create(request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Meter in unit with id 10");

        verify(meterMapper, never()).toEntity(any());
        verify(meterRepository, never()).save(any());
    }

    @Test
    void getMeterByIdShouldReturnMeter() {
        MeterResponseDto expectedResponse = new MeterResponseDto(
                100L,
                1L,
                10L,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Water meter",
                null,
                true);

        when(meterRepository.findById(100L))
                .thenReturn(Optional.of(meter));

        when(meterMapper.toDto(meter))
                .thenReturn(expectedResponse);

        MeterResponseDto result = meterService.getMeterById(100L);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getMeterByIdShouldThrowWhenMeterDoesNotExist() {
        when(meterRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> meterService.getMeterById(100L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meter with id 100");
    }

    @Test
    void toggleMeterActiveStatusShouldDeactivateActiveMeter() {
        MeterResponseDto expectedResponse = createMeterResponse(false);

        when(meterRepository.findById(100L))
                .thenReturn(Optional.of(meter));

        when(meterRepository.save(meter))
                .thenReturn(meter);

        when(meterMapper.toDto(meter))
                .thenReturn(expectedResponse);

        MeterResponseDto result =
                meterService.toggleMeterActiveStatus(100L);

        assertThat(meter.isActive()).isFalse();
        assertThat(result.active()).isFalse();

        verify(meterRepository).save(meter);
    }

    @Test
    void toggleMeterActiveStatusShouldActivateInactiveMeter() {
        meter.setActive(false);

        MeterResponseDto expectedResponse = createMeterResponse(true);

        when(meterRepository.findById(100L))
                .thenReturn(Optional.of(meter));

        when(meterRepository.save(meter))
                .thenReturn(meter);

        when(meterMapper.toDto(meter))
                .thenReturn(expectedResponse);

        MeterResponseDto result =
                meterService.toggleMeterActiveStatus(100L);

        assertThat(meter.isActive()).isTrue();
        assertThat(result.active()).isTrue();

        verify(meterRepository).save(meter);
    }

    @Test
    void getMetersShouldReturnMappedMeters() {
        MeterFilterDto filter = new MeterFilterDto(
                1L,
                10L,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                true);

        List<Meter> meters = List.of(meter);

        List<MeterResponseDto> expectedResponse =
                List.of(createMeterResponse(true));

        when(meterRepository.findAll(any(Specification.class)))
                .thenReturn(meters);

        when(meterMapper.toDtoList(meters))
                .thenReturn(expectedResponse);

        List<MeterResponseDto> result =
                meterService.getMeters(filter);

        assertThat(result).isEqualTo(expectedResponse);

        verify(meterRepository)
                .findAll(any(Specification.class));
    }

    @Test
    void createReadingShouldSaveReading() {
        LocalDate readingDate = LocalDate.of(2026, 3, 1);

        CreateMeterReadingRequestDto request =
                new CreateMeterReadingRequestDto(
                        readingDate,
                        new BigDecimal("25.321"),
                        "Monthly reading");

        MeterReading meterReading = new MeterReading();

        MeterReadingResponseDto expectedResponse =
                new MeterReadingResponseDto(
                        1L,
                        100L,
                        readingDate,
                        new BigDecimal("25.321"),
                        "Monthly reading");

        when(meterReadingRepository.existsByMeterIdAndReadingDate(
                100L, readingDate))
                .thenReturn(false);

        when(meterRepository.findById(100L))
                .thenReturn(Optional.of(meter));

        when(meterReadingMapper.toEntity(request))
                .thenReturn(meterReading);

        when(meterReadingRepository.save(meterReading))
                .thenReturn(meterReading);

        when(meterReadingMapper.toDto(meterReading))
                .thenReturn(expectedResponse);

        MeterReadingResponseDto result =
                meterService.createReading(100L, request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(meterReading.getMeter()).isEqualTo(meter);

        verify(meterReadingRepository).save(meterReading);
    }

    @Test
    void createReadingShouldThrowWhenDuplicateExists() {
        LocalDate readingDate = LocalDate.of(2026, 3, 1);

        CreateMeterReadingRequestDto request =
                createReadingRequest(readingDate);

        when(meterReadingRepository.existsByMeterIdAndReadingDate(
                100L, readingDate))
                .thenReturn(true);

        assertThatThrownBy(
                () -> meterService.createReading(100L, request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(meterRepository, never()).findById(any());
        verify(meterReadingRepository, never()).save(any());
    }

    @Test
    void createReadingShouldThrowWhenReadingDateIsInFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        CreateMeterReadingRequestDto request =
                createReadingRequest(futureDate);

        when(meterReadingRepository.existsByMeterIdAndReadingDate(
                100L, futureDate))
                .thenReturn(false);

        assertThatThrownBy(
                () -> meterService.createReading(100L, request))
                .isInstanceOf(InvalidMeterDataException.class)
                .hasMessageContaining("after today");

        verify(meterRepository, never()).findById(any());
        verify(meterReadingRepository, never()).save(any());
    }

    @Test
    void createReadingShouldThrowWhenMeterDoesNotExist() {
        LocalDate readingDate = LocalDate.of(2026, 3, 1);

        CreateMeterReadingRequestDto request =
                createReadingRequest(readingDate);

        when(meterReadingRepository.existsByMeterIdAndReadingDate(
                100L, readingDate))
                .thenReturn(false);

        when(meterRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> meterService.createReading(100L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meter with id 100");

        verify(meterReadingRepository, never()).save(any());
    }

    @Test
    void createReadingShouldThrowWhenMeterIsInactive() {
        meter.setActive(false);

        LocalDate readingDate = LocalDate.of(2026, 3, 1);

        CreateMeterReadingRequestDto request =
                createReadingRequest(readingDate);

        when(meterReadingRepository.existsByMeterIdAndReadingDate(
                100L, readingDate))
                .thenReturn(false);

        when(meterRepository.findById(100L))
                .thenReturn(Optional.of(meter));

        assertThatThrownBy(
                () -> meterService.createReading(100L, request))
                .isInstanceOf(InactiveEntityException.class)
                .hasMessageContaining("Meter with id 100 is inactive");

        verify(meterReadingRepository, never()).save(any());
    }

    @Test
    void getReadingByIdShouldReturnReading() {
        LocalDate readingDate = LocalDate.of(2026, 3, 1);

        MeterReading reading = createMeterReading(
                1L,
                readingDate,
                "25.321");

        MeterReadingResponseDto expectedResponse =
                new MeterReadingResponseDto(
                        1L,
                        100L,
                        readingDate,
                        new BigDecimal("25.321"),
                        "Test reading");

        when(meterReadingRepository.findById(1L))
                .thenReturn(Optional.of(reading));

        when(meterReadingMapper.toDto(reading))
                .thenReturn(expectedResponse);

        MeterReadingResponseDto result =
                meterService.getReadingById(1L);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getReadingByIdShouldThrowWhenReadingDoesNotExist() {
        when(meterReadingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> meterService.getReadingById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meter reading with id 1");
    }

    @Test
    void getReadingsShouldReturnMappedReadings() {
        MeterReadingFilterDto filter =
                new MeterReadingFilterDto(
                        100L,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 3, 31));

        MeterReading reading = createMeterReading(
                1L,
                LocalDate.of(2026, 2, 1),
                "20.000");

        List<MeterReading> readings = List.of(reading);

        List<MeterReadingResponseDto> expectedResponse =
                List.of(new MeterReadingResponseDto(
                        1L,
                        100L,
                        LocalDate.of(2026, 2, 1),
                        new BigDecimal("20.000"),
                        "Test reading"));

        when(meterReadingRepository.findAll(
                any(Specification.class)))
                .thenReturn(readings);

        when(meterReadingMapper.toDtoList(readings))
                .thenReturn(expectedResponse);

        List<MeterReadingResponseDto> result =
                meterService.getReadings(filter);

        assertThat(result).isEqualTo(expectedResponse);

        verify(meterReadingRepository)
                .findAll(any(Specification.class));
    }

    @Test
    void getReadingsShouldThrowWhenToDateIsBeforeFromDate() {
        MeterReadingFilterDto filter =
                new MeterReadingFilterDto(
                        100L,
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 2, 1));

        assertThatThrownBy(
                () -> meterService.getReadings(filter))
                .isInstanceOf(InvalidMeterDataException.class)
                .hasMessageContaining(
                        "Meter reading end date 2026-02-01"
                                + " is before start date 2026-03-01");

        verify(meterReadingRepository, never())
                .findAll(any(Specification.class));
    }

    private CreateMeterRequestDto createMeterRequest(Long unitId) {
        return new CreateMeterRequestDto(
                1L,
                unitId,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Water meter",
                null);
    }

    private CreateMeterReadingRequestDto createReadingRequest(
            LocalDate readingDate) {

        return new CreateMeterReadingRequestDto(
                readingDate,
                new BigDecimal("25.321"),
                "Test reading");
    }

    private MeterResponseDto createMeterResponse(boolean active) {
        return new MeterResponseDto(
                100L,
                1L,
                10L,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Water meter",
                null,
                active);
    }

    private MeterReading createMeterReading(
            Long id,
            LocalDate readingDate,
            String value) {

        MeterReading reading = new MeterReading();
        reading.setId(id);
        reading.setMeter(meter);
        reading.setReadingDate(readingDate);
        reading.setValue(new BigDecimal(value));
        reading.setNotes("Test reading");

        return reading;
    }
}
