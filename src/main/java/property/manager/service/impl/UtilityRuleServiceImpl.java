package property.manager.service.impl;

import static property.manager.model.AllocationMethod.AREA_SHARE;
import static property.manager.model.AllocationMethod.EQUAL_PER_UNIT;
import static property.manager.model.AllocationMethod.SOURCE_METER_AREA_SHARE;
import static property.manager.model.AllocationMethod.SUBMETER_USAGE;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import property.manager.dto.utility.rule.CreateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UpdateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UtilityRuleEndMonthRequestDto;
import property.manager.dto.utility.rule.UtilityRuleResponseDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InactiveEntityException;
import property.manager.exception.InvalidUtilityRuleDataException;
import property.manager.mapper.UtilityRuleMapper;
import property.manager.model.AllocationMethod;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;
import property.manager.model.Property;
import property.manager.model.Unit;
import property.manager.model.UtilityRule;
import property.manager.model.UtilityType;
import property.manager.repository.PropertyRepository;
import property.manager.repository.UnitRepository;
import property.manager.repository.UtilityRuleRepository;
import property.manager.service.UtilityRuleService;

@Service
@RequiredArgsConstructor
public class UtilityRuleServiceImpl implements UtilityRuleService {

    private final UtilityRuleRepository utilityRuleRepository;
    private final PropertyRepository propertyRepository;
    private final UtilityRuleMapper utilityRuleMapper;
    private final UnitRepository unitRepository;

    @Override
    public UtilityRuleResponseDto createUtilityRule(
            CreateUtilityRuleRequestDto request) {
        validateMeterRequired(request.unitMeterRequired(), request.propertyMeterRequired(),
                request.measurementType(), request.meterPurpose());
        checkDates(request.startMonth(), request.endMonth());
        Property property = getProperty(request.propertyId());
        checkDuplicate(request);
        List<Unit> units = getAndValidateUnits(request.unitIds(), request.propertyId());
        checkAllocationMethod(request.allocationMethod(), request.unitMeterRequired(),
                request.propertyMeterRequired(), units);
        UtilityRule rule = utilityRuleMapper.toEntity(request);
        rule.setProperty(property);
        rule.setUnits(units);
        UtilityRule savedRule = utilityRuleRepository.save(rule);
        return utilityRuleMapper.toDto(savedRule);
    }

    @Override
    public List<UtilityRuleResponseDto> getUtilityRules(Long propertyId, UtilityType utilityType) {
        Property property = getProperty(propertyId);
        List<UtilityRule> rules;
        if (utilityType == null) {
            rules = utilityRuleRepository.findAllByPropertyId(propertyId);
        } else {
            rules = utilityRuleRepository
                    .findAllByPropertyIdAndUtilityType(propertyId, utilityType);
        }
        return utilityRuleMapper.toDtoList(rules);
    }

    @Override
    public UtilityRuleResponseDto getUtilityRule(Long id) {
        UtilityRule rule = findUtilityRule(id);
        return utilityRuleMapper.toDto(rule);
    }

    @Override
    public UtilityRuleResponseDto setUtilityRuleEndMonth(
            Long id, UtilityRuleEndMonthRequestDto request) {
        UtilityRule rule = findUtilityRule(id);
        if (!rule.isActive()) {
            throw new InactiveEntityException("Property utility rule with id "
                    + id + " is inactive");
        }
        checkDates(YearMonth.now(), request.endMonth());
        rule.setActive(false);
        rule.setEndMonth(request.endMonth());
        UtilityRule savedRule = utilityRuleRepository.save(rule);
        return utilityRuleMapper.toDto(savedRule);
    }

    @Override
    public UtilityRuleResponseDto updateUtilityRule(
            Long id, UpdateUtilityRuleRequestDto request) {
        validateMeterRequired(request.unitMeterRequired(), request.propertyMeterRequired(),
                request.measurementType(), request.meterPurpose());
        checkDates(request.startMonth(), request.endMonth());
        checkDuplicate(id, request);
        Property property = getProperty(request.propertyId());
        UtilityRule rule = findUtilityRule(id);
        List<Unit> units = getAndValidateUnits(request.unitIds(), request.propertyId());
        checkAllocationMethod(request.allocationMethod(), request.unitMeterRequired(),
                request.propertyMeterRequired(), units);
        utilityRuleMapper.updateEntity(request, rule);
        rule.setProperty(property);
        rule.setUnits(units);
        UtilityRule savedRule = utilityRuleRepository.save(rule);
        return utilityRuleMapper.toDto(savedRule);
    }

    private void validateMeterRequired(boolean unitMeterRequired, boolean propertyMeterRequired,
                                       MeasurementType measurementType, MeterPurpose purpose) {
        if (unitMeterRequired && propertyMeterRequired) {
            throw new InvalidUtilityRuleDataException("Utility rule cannot require both unit "
                    + "meter and property meter.");
        }
        if ((unitMeterRequired || propertyMeterRequired)
                && (measurementType == null || purpose == null)) {
            throw new InvalidUtilityRuleDataException("If a meter is required, measurement type "
                    + "and meter purpose cannot be null");
        }
        if ((!unitMeterRequired && !propertyMeterRequired)
                && (measurementType != null || purpose != null)) {
            throw new InvalidUtilityRuleDataException("If meter is  not required measurement type"
            + " and meter purpose must be null");
        }
    }

    private void checkDates(YearMonth startMonth, YearMonth endMonth) {
        if (startMonth != null && endMonth != null && endMonth.isBefore(startMonth)) {
            throw new InvalidUtilityRuleDataException("Property utility rule end month "
                    + endMonth + " is before start month " + startMonth);
        }
    }

    private void checkDuplicate(CreateUtilityRuleRequestDto request) {
        List<UtilityRule> overlappingRules =
                utilityRuleRepository.findOverlappingRules(
                        request.propertyId(), request.utilityType(), request.startMonth(),
                        request.endMonth());
        if (!overlappingRules.isEmpty()) {
            throw new EntityAlreadyExistsException(
                    "Property utility rule for property with id " + request.propertyId()
                            + " and utility type " + request.utilityType()
                            + " overlaps with existing rule");
        }
    }

    private void checkDuplicate(Long id, UpdateUtilityRuleRequestDto request) {
        List<UtilityRule> overlappingRules =
                utilityRuleRepository.findOverlappingRulesExcludingId(
                        request.propertyId(), request.utilityType(), request.startMonth(),
                        request.endMonth(), id);
        if (!overlappingRules.isEmpty()) {
            throw new EntityAlreadyExistsException(
                    "Property utility rule with id " + id + " for property with id "
                            + request.propertyId() + " and utility type " + request.utilityType()
                            + " overlaps with existing rule");
        }
    }

    private Property getProperty(Long id) {
        Property property = propertyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Property with id " + id
                        + " doesn't exist in database"));
        if (!property.isActive()) {
            throw new InactiveEntityException("Property with id " + id
                    + " is inactive");
        }
        return property;
    }

    private void checkAllocationMethod(
            AllocationMethod allocationMethod, boolean unitMeterRequired,
            boolean propertyMeterRequired, List<Unit> units) {
        String exceptionText = "For allocation method "
                + allocationMethod.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        if ((unitMeterRequired || propertyMeterRequired) && (allocationMethod == AREA_SHARE
                || allocationMethod == EQUAL_PER_UNIT)) {
            throw new InvalidUtilityRuleDataException(exceptionText + ": meters are not required");
        }
        if (!unitMeterRequired && allocationMethod == SUBMETER_USAGE) {
            throw new InvalidUtilityRuleDataException(exceptionText + ": unit meter is required");
        }
        if (!propertyMeterRequired && allocationMethod == SOURCE_METER_AREA_SHARE) {
            throw new InvalidUtilityRuleDataException(exceptionText
                    + ": property meter is required");
        }
        if (allocationMethod == AREA_SHARE || allocationMethod == SOURCE_METER_AREA_SHARE) {
            for (Unit unit : units) {
                if (unit.getArea() == null || unit.getArea().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidUtilityRuleDataException("Unit with id " + unit.getId()
                            + " must have positive area for area share allocation");
                }
            }
        }
    }

    private UtilityRule findUtilityRule(Long id) {
        return utilityRuleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Property utility rule with id "
                        + id + " doesn't exist in database"));
    }

    private List<Unit> getAndValidateUnits(List<Long> unitsIds, Long propertyId) {
        if (new HashSet<>(unitsIds).size() != unitsIds.size()) {
            throw new InvalidUtilityRuleDataException("Unit ids contain duplicates");
        }
        List<Unit> units = unitRepository.findAllById(unitsIds);
        if (units.size() != unitsIds.size()) {
            throw new EntityNotFoundException("One or more units don't exist in database");
        }
        for (Unit unit : units) {
            if (!unit.getProperty().getId().equals(propertyId)) {
                throw new InvalidUtilityRuleDataException("Unit with id " + unit.getId()
                        + " doesn't belong to property with id " + propertyId);
            }
        }
        return units;
    }

}
