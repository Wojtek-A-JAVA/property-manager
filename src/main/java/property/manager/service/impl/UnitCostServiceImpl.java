package property.manager.service.impl;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property.manager.dto.cost.unit.CreateUnitCostRequestDto;
import property.manager.dto.cost.unit.CreateUnitCostsRequestDto;
import property.manager.dto.cost.unit.UnitCostFilterRequestDto;
import property.manager.dto.cost.unit.UnitCostResponseDto;
import property.manager.dto.cost.unit.UpdateUnitCostNotesRequestDto;
import property.manager.dto.cost.unit.UpdateUnitCostRequestDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InvalidUnitCostDataException;
import property.manager.mapper.UnitCostMapper;
import property.manager.model.Meter;
import property.manager.model.MeterReading;
import property.manager.model.PropertyCost;
import property.manager.model.Unit;
import property.manager.model.UnitCost;
import property.manager.model.UtilityInvoice;
import property.manager.model.UtilityRule;
import property.manager.model.UtilityType;
import property.manager.repository.MeterReadingRepository;
import property.manager.repository.MeterRepository;
import property.manager.repository.PropertyCostRepository;
import property.manager.repository.UnitCostRepository;
import property.manager.repository.UnitRepository;
import property.manager.repository.UtilityRuleRepository;
import property.manager.service.UnitCostService;
import property.manager.specification.UnitCostSpecification;

@Service
@RequiredArgsConstructor
public class UnitCostServiceImpl implements UnitCostService {

    private final UnitRepository unitRepository;
    private final PropertyCostRepository propertyCostRepository;
    private final UnitCostRepository unitCostRepository;
    private final UtilityRuleRepository utilityRuleRepository;
    private final UnitCostMapper unitCostMapper;
    private final MeterRepository meterRepository;
    private final MeterReadingRepository meterReadingRepository;

    @Override
    public UnitCostResponseDto createUnitCost(CreateUnitCostRequestDto request) {
        Unit unit = getUnit(request.unitId());
        PropertyCost propertyCost = getPropertyCost(request.propertyCostId(), unit);
        checkDuplicate(request.unitId(), propertyCost, request.utilityType());
        UtilityRule utilityRule = getUtilityRule(propertyCost.getProperty().getId(),
                request.utilityType(), propertyCost.getBillingMonth());
        checkUnitParticipation(utilityRule, unit);
        UnitCost unitCost = new UnitCost();
        unitCost.setUnit(unit);
        unitCost.setPropertyCost(propertyCost);
        unitCost.setBillingMonth(propertyCost.getBillingMonth());
        unitCost.setUtilityRule(utilityRule);
        setUnitCostAmountAndConsumption(unitCost, utilityRule, propertyCost, unit);
        unitCost.setNotes(request.notes());
        UnitCost savedUnitCost = unitCostRepository.save(unitCost);
        return unitCostMapper.toDto(savedUnitCost);
    }

    @Override
    @Transactional
    public List<UnitCostResponseDto> createUnitCosts(CreateUnitCostsRequestDto request) {
        PropertyCost propertyCost = propertyCostRepository.findById(request.propertyCostId())
                .orElseThrow(() -> new EntityNotFoundException("Property cost with id "
                        + request.propertyCostId() + " doesn't exist in database"));
        List<UnitCost> unitCostList = createUnitCostEntities(propertyCost, request.utilityType());
        List<UnitCostResponseDto> unitCostResponseDtoList = new ArrayList<>();
        for (UnitCost unitCost : unitCostList) {
            unitCost.setNotes(request.notes());
            unitCostResponseDtoList.add(unitCostMapper.toDto(unitCost));
        }
        return unitCostResponseDtoList;
    }

    @Override
    public UnitCostResponseDto getUnitCost(Long id) {
        UnitCost unitCost = findUnitCost(id);
        return unitCostMapper.toDto(unitCost);
    }

    @Override
    public List<UnitCostResponseDto> getUnitCosts(UnitCostFilterRequestDto filter) {
        Specification<UnitCost> specification = UnitCostSpecification
                .hasPropertyId(filter.propertyId())
                .and(UnitCostSpecification.hasUnitId(filter.unitId()))
                .and(UnitCostSpecification.hasBillingMonthBetween(
                        filter.billingMonthFrom(), filter.billingMonthTo()))
                .and(UnitCostSpecification.hasAmountBetween(
                        filter.amountFrom(), filter.amountTo()))
                .and(UnitCostSpecification.hasUtilityTypes(filter.utilityTypes()))
                .and(UnitCostSpecification.hasAllocationMethod(filter.allocationMethod()))
                .and(UnitCostSpecification.hasMeterId(filter.meterId()));
        List<UnitCost> unitCosts = unitCostRepository.findAll(specification);
        return unitCostMapper.toDtoList(unitCosts);
    }

    @Override
    public UnitCostResponseDto updateUnitCost(Long id, UpdateUnitCostRequestDto request) {
        UnitCost unitCost = findUnitCost(id);
        Unit unit = getUnit(request.unitId());
        unitCost.setUnit(unit);
        PropertyCost propertyCost = getPropertyCost(request.propertyCostId(), unit);
        unitCost.setPropertyCost(propertyCost);
        checkDuplicate(unitCost.getId(), request);
        UtilityRule utilityRule = getUtilityRule(propertyCost.getProperty().getId(),
                request.utilityType(), propertyCost.getBillingMonth());
        unitCost.setUtilityRule(utilityRule);
        checkUnitParticipation(utilityRule, unit);
        unitCost.setBillingMonth(propertyCost.getBillingMonth());
        unitCost.setMeter(null);
        unitCost.setConsumption(null);
        setUnitCostAmountAndConsumption(unitCost, utilityRule, propertyCost, unit);
        String notes = "Unit cost was updated " + LocalDate.now();
        if (unitCost.getNotes() == null) {
            unitCost.setNotes(notes);
        } else {
            unitCost.setNotes(unitCost.getNotes() + "; " + notes);
        }
        UnitCost savedUnitCost = unitCostRepository.save(unitCost);
        return unitCostMapper.toDto(savedUnitCost);
    }

    @Override
    public UnitCostResponseDto updateUnitCostNotes(Long id, UpdateUnitCostNotesRequestDto request) {
        UnitCost unitCost = findUnitCost(id);
        unitCost.setNotes(request.notes());
        UnitCost savedUnitCost = unitCostRepository.save(unitCost);
        return unitCostMapper.toDto(savedUnitCost);
    }

    @Override
    @Transactional
    public List<UnitCost> createUnitCostEntities(
            PropertyCost propertyCost, UtilityType utilityType) {
        List<UnitCost> unitCostList = new ArrayList<>();
        UtilityRule utilityRule = getUtilityRule(propertyCost.getProperty().getId(), utilityType,
                propertyCost.getBillingMonth());
        List<Unit> units = utilityRule.getUnits();
        for (Unit unit : units) {
            checkDuplicate(unit.getId(), propertyCost, utilityRule.getUtilityType());
            UnitCost unitCost = new UnitCost();
            unitCost.setUnit(unit);
            unitCost.setPropertyCost(propertyCost);
            unitCost.setBillingMonth(propertyCost.getBillingMonth());
            unitCost.setUtilityRule(utilityRule);
            setUnitCostAmountAndConsumption(unitCost, utilityRule, propertyCost, unit);
            UnitCost savedUnitCost = unitCostRepository.save(unitCost);
            unitCostList.add(savedUnitCost);
        }
        return unitCostList;
    }

    private Unit getUnit(Long id) {
        return unitRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Unit with id " + id
                        + " doesn't exist in database"));
    }

    private UnitCost findUnitCost(Long id) {
        return unitCostRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Unit cost with id " + id
                        + " doesn't exist in database"));
    }

    private PropertyCost getPropertyCost(Long propertyCostId, Unit unit) {
        PropertyCost propertyCost = propertyCostRepository.findById(propertyCostId).orElseThrow(
                () -> new EntityNotFoundException("Property cost with id " + propertyCostId
                        + " doesn't exist in database"));
        if (!propertyCost.getProperty().getId().equals(unit.getProperty().getId())) {
            throw new InvalidUnitCostDataException("Property cost with id " + propertyCostId
                    + " doesn't belong to property of unit with id " + unit.getId());
        }
        return propertyCost;
    }

    private void checkDuplicate(Long unitId, PropertyCost propertyCost, UtilityType utilityType) {
        if (unitCostRepository.existsByUnitIdAndPropertyCostIdAndUtilityRuleUtilityType(
                unitId, propertyCost.getId(), utilityType)) {
            throw new EntityAlreadyExistsException("Unit cost for unit with id " + unitId
                    + ", property cost with id " + propertyCost.getId() + " and utility type "
                    + utilityType.name().toLowerCase(Locale.ROOT).replace('_', ' ')
                    + " already exists in database");
        }
    }

    private void checkDuplicate(Long unitCostId, UpdateUnitCostRequestDto request) {
        if (unitCostRepository.existsByUnitIdAndPropertyCostIdAndUtilityRuleUtilityTypeAndIdNot(
                request.unitId(), request.propertyCostId(), request.utilityType(), unitCostId)) {
            throw new EntityAlreadyExistsException("Unit cost with id " + unitCostId
                    + " for unit with id " + request.unitId() + ", property cost with id "
                    + request.propertyCostId() + " and utility type "
                    + request.utilityType().name().toLowerCase(Locale.ROOT).replace('_', ' ')
                    + " already exists in database");
        }
    }

    private UtilityRule getUtilityRule(
            Long propertyId, UtilityType utilityType, YearMonth billingMonth) {
        return utilityRuleRepository.findByPropertyIdAndUtilityTypeAndBillingMonth(
                        propertyId, utilityType, billingMonth)
                .orElseThrow(() -> new EntityNotFoundException("Utility rule for property with id "
                        + propertyId + ", utility type "
                        + utilityType.name().toLowerCase(Locale.ROOT).replace('_', ' ')
                        + " and for billing month " + billingMonth
                        + " doesn't exist in database"));
    }

    private void checkUnitParticipation(UtilityRule utilityRule, Unit unit) {
        boolean participates = utilityRule.getUnits().stream()
                .anyMatch(ruleUnit -> ruleUnit.getId().equals(unit.getId()));
        if (!participates) {
            throw new InvalidUnitCostDataException("Utility rule with id " + utilityRule.getId()
                    + " is not for unit with id " + unit.getId());
        }
    }

    private BigDecimal getUnitsArea(UtilityRule utilityRule) {
        BigDecimal unitsArea = BigDecimal.ZERO;
        for (Unit u : utilityRule.getUnits()) {
            unitsArea = unitsArea.add(u.getArea());
        }
        return unitsArea;
    }

    private BigDecimal calculateEqualPerUnit(
            PropertyCost propertyCost, UtilityRule utilityRule) {
        return propertyCost.getAmount().divide(
                BigDecimal.valueOf(utilityRule.getUnits().size()), 2, HALF_UP);
    }

    private BigDecimal calculateAreaShare(
            PropertyCost propertyCost, UtilityRule utilityRule, Unit unit) {
        BigDecimal unitsArea = getUnitsArea(utilityRule);
        return unit.getArea().multiply(propertyCost.getAmount())
                .divide(unitsArea, 2, HALF_UP);
    }

    private Meter getUnitMeter(Unit unit, UtilityRule utilityRule) {
        return meterRepository.findByUnitIdAndPurposeAndMeasurementTypeAndActiveTrue(
                unit.getId(), utilityRule.getMeterPurpose(),
                utilityRule.getMeasurementType())
                .orElseThrow(() -> new EntityNotFoundException("Meter for unit with id "
                        + unit.getId() + " for " + utilityRule.getMeterPurpose()
                        .name().toLowerCase(Locale.ROOT).replace('_', ' ')
                        + " and " + utilityRule.getMeasurementType()
                        .name().toLowerCase(Locale.ROOT).replace('_', ' ')
                        + " doesn't exist in database"));
    }

    private Meter getPropertyMeter(UtilityRule utilityRule) {
        return meterRepository.findPropertyMeter(utilityRule.getProperty().getId(),
                utilityRule.getMeasurementType(), utilityRule.getMeterPurpose())
                .orElseThrow(() -> new EntityNotFoundException("Meter for property with id "
                        + utilityRule.getProperty().getId() + " for "
                        + utilityRule.getMeterPurpose()
                        .name().toLowerCase(Locale.ROOT).replace('_', ' ')
                        + " and " + utilityRule.getMeasurementType()
                        .name().toLowerCase(Locale.ROOT).replace('_', ' ')
                        + " doesn't exist in database"));
    }

    private BigDecimal calculateConsumption(Meter meter, PropertyCost propertyCost) {
        LocalDate currentReadingDate =
                propertyCost.getBillingMonth().plusMonths(1).atDay(1);
        MeterReading currentReading = meterReadingRepository
                .findByMeterIdAndReadingDate(meter.getId(), currentReadingDate)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Meter reading for meter with id " + meter.getId()
                                + " and date " + currentReadingDate
                                + " doesn't exist in database"));
        MeterReading previousReading = meterReadingRepository
                .findFirstByMeterIdAndReadingDateBeforeOrderByReadingDateDesc(
                        meter.getId(), currentReadingDate)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Previous meter reading for meter with id "
                                + meter.getId() + " doesn't exist in database"));
        return currentReading.getValue().subtract(previousReading.getValue());
    }

    private BigDecimal calculateSourceMeterAreaShare(
            Meter meter, PropertyCost propertyCost, UtilityRule utilityRule, Unit unit) {
        BigDecimal consumption = calculateConsumption(meter, propertyCost);
        BigDecimal unitsArea = getUnitsArea(utilityRule);
        return consumption.multiply(unit.getArea()).divide(unitsArea, 3, HALF_UP);
    }

    private BigDecimal getAmount(BigDecimal consumption, UtilityInvoice utilityInvoice) {
        return consumption.multiply(utilityInvoice.getUnitPrice()).setScale(2, HALF_UP);
    }

    private void setUnitCostAmountAndConsumption(
            UnitCost unitCost, UtilityRule utilityRule, PropertyCost propertyCost, Unit unit) {
        switch (utilityRule.getAllocationMethod()) {
            case EQUAL_PER_UNIT -> {
                unitCost.setAmount(calculateEqualPerUnit(propertyCost, utilityRule));
            }
            case AREA_SHARE -> {
                unitCost.setAmount(calculateAreaShare(propertyCost, utilityRule, unit));
            }
            case SUBMETER_USAGE -> {
                unitCost.setMeter(getUnitMeter(unit, utilityRule));
                unitCost.setConsumption(calculateConsumption(unitCost.getMeter(), propertyCost));
                unitCost.setAmount(getAmount(
                        unitCost.getConsumption(), propertyCost.getUtilityInvoice()));
            }
            case SOURCE_METER_AREA_SHARE -> {
                unitCost.setMeter(getPropertyMeter(utilityRule));
                unitCost.setConsumption(calculateSourceMeterAreaShare(
                        unitCost.getMeter(), propertyCost, utilityRule, unit));
                unitCost.setAmount(getAmount(
                        unitCost.getConsumption(), propertyCost.getUtilityInvoice()));
            }
            default -> {
                throw new InvalidUnitCostDataException("Unsupported allocation method: "
                        + utilityRule.getAllocationMethod());
            }
        }
    }
}
