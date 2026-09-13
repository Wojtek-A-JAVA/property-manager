package property.manager.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
import property.manager.model.Meter;
import property.manager.model.MeterReading;
import property.manager.model.Property;
import property.manager.model.Unit;
import property.manager.repository.MeterReadingRepository;
import property.manager.repository.MeterRepository;
import property.manager.repository.PropertyRepository;
import property.manager.repository.UnitRepository;
import property.manager.service.MeterService;
import property.manager.specification.MeterReadingSpecification;
import property.manager.specification.MeterSpecification;

@Service
@RequiredArgsConstructor
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final MeterMapper meterMapper;
    private final MeterReadingRepository meterReadingRepository;
    private final MeterReadingMapper meterReadingMapper;

    @Override
    public MeterResponseDto create(CreateMeterRequestDto request) {
        Property property = getActiveProperty(request.propertyId());
        Unit unit = null;
        if (request.unitId() != null) {
            unit = getActiveUnitForProperty(request.unitId(), property.getId());
        }
        checkDuplicates(request);
        Meter meter = meterMapper.toEntity(request);
        meter.setProperty(property);
        meter.setUnit(unit);
        Meter savedMeter = meterRepository.save(meter);
        return meterMapper.toDto(savedMeter);
    }

    @Override
    public MeterResponseDto getMeterById(Long id) {
        Meter meter = findMeterById(id);
        return meterMapper.toDto(meter);
    }

    @Override
    public List<MeterResponseDto> getMeters(MeterFilterDto filter) {
        Specification<Meter> specification = MeterSpecification.hasPropertyId(filter.propertyId())
                .and(MeterSpecification.hasUnitId(filter.unitId()))
                .and(MeterSpecification.hasMeasurementType(filter.measurementType()))
                .and(MeterSpecification.hasPurpose(filter.purpose()))
                .and(MeterSpecification.isActive(filter.active()));
        List<Meter> meters = meterRepository.findAll(specification);
        return meterMapper.toDtoList(meters);
    }

    @Override
    public MeterResponseDto toggleMeterActiveStatus(Long id) {
        Meter meter = findMeterById(id);
        meter.setActive(!meter.isActive());
        Meter savedMeter = meterRepository.save(meter);
        return meterMapper.toDto(savedMeter);
    }

    @Override
    public MeterReadingResponseDto createReading(Long id, CreateMeterReadingRequestDto request) {
        if (meterReadingRepository.existsByMeterIdAndReadingDate(
                id, request.readingDate())) {
            throw new EntityAlreadyExistsException("Meter reading for meter with id " + id
                    + " and date " + request.readingDate()
                    + " already exists in database");
        }
        if (request.readingDate().isAfter(LocalDate.now())) {
            throw new InvalidMeterDataException("Meter reading is after today");
        }
        Meter meter = findMeterById(id);
        if (!meter.isActive()) {
            throw new InactiveEntityException("Meter with id " + id + " is inactive");
        }
        MeterReading meterReading = meterReadingMapper.toEntity(request);
        meterReading.setMeter(meter);
        MeterReading savedMeterReading = meterReadingRepository.save(meterReading);
        return meterReadingMapper.toDto(savedMeterReading);
    }

    @Override
    public MeterReadingResponseDto getReadingById(Long readingId) {
        MeterReading meterReading = meterReadingRepository.findById(readingId).orElseThrow(
                () -> new EntityNotFoundException("Meter reading with id " + readingId
                        + " doesn't exist in database")
        );
        return meterReadingMapper.toDto(meterReading);
    }

    @Override
    public List<MeterReadingResponseDto> getReadings(MeterReadingFilterDto filter) {
        checkDates(filter.fromDate(), filter.toDate());
        Specification<MeterReading> specification = MeterReadingSpecification
                .hasMeterId(filter.meterId())
                .and(MeterReadingSpecification
                        .isWithinPeriod(filter.fromDate(), filter.toDate()));
        List<MeterReading> meterReadings = meterReadingRepository.findAll(specification);
        return meterReadingMapper.toDtoList(meterReadings);
    }

    private void checkDuplicates(CreateMeterRequestDto request) {
        String endMessageException = " for measurement type "
                + request.measurementType().name().toLowerCase() + " for "
                + request.purpose().name().toLowerCase() + " purpose"
                + " is already in database";
        if (request.unitId() == null) {
            if (meterRepository
                    .existsByPropertyIdAndUnitIsNullAndMeasurementTypeAndPurposeAndActiveTrue(
                            request.propertyId(), request.measurementType(), request.purpose())) {
                throw new EntityAlreadyExistsException("Meter in property with id "
                        + request.propertyId() + endMessageException);
            }
        } else {
            if (meterRepository.existsByUnitIdAndMeasurementTypeAndPurposeAndActiveTrue(
                    request.unitId(), request.measurementType(), request.purpose())) {
                throw new EntityAlreadyExistsException("Meter in unit with id " + request.unitId()
                        + endMessageException);
            }
        }

    }

    private Property getActiveProperty(Long id) {
        Property property = propertyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Property with id " + id
                        + " not found in database"));
        if (!property.isActive()) {
            throw new InactiveEntityException("Property with id " + id + " is inactive");
        }
        return property;
    }

    private Unit getActiveUnitForProperty(Long unitId, Long propertyId) {
        Unit unit = unitRepository.findById(unitId).orElseThrow(
                () -> new EntityNotFoundException("Unit with id " + unitId
                        + " not found in database"));
        if (!unit.getProperty().getId().equals(propertyId)) {
            throw new InvalidMeterDataException("Unit with id " + unitId
                    + " is not in property with id " + propertyId);
        }
        if (!unit.isActive()) {
            throw new InactiveEntityException("Unit with id " + unitId + " is inactive");
        }
        return unit;
    }

    private Meter findMeterById(Long id) {
        return meterRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Meter with id " + id
                        + " doesn't exist in database")
        );
    }

    private void checkDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new InvalidMeterDataException("Meter reading end date " + endDate
                    + " is before start date " + startDate);
        }
    }
}
