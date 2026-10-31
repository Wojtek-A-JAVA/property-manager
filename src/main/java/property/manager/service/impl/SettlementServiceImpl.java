package property.manager.service.impl;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property.manager.dto.settlement.CreateSettlementRequestDto;
import property.manager.dto.settlement.RecalculateSettlementRequestDto;
import property.manager.dto.settlement.SettlementResponseDto;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InvalidSettlementDataException;
import property.manager.model.Meter;
import property.manager.model.MeterReading;
import property.manager.model.PropertyCost;
import property.manager.model.Unit;
import property.manager.model.UnitCost;
import property.manager.model.UtilityInvoice;
import property.manager.model.UtilityRule;
import property.manager.model.UtilityRuleHistory;
import property.manager.model.UtilityType;
import property.manager.service.MeterService;
import property.manager.service.PropertyCostService;
import property.manager.service.SettlementService;
import property.manager.service.UnitCostService;
import property.manager.service.UtilityInvoiceService;
import property.manager.service.UtilityRuleService;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private static final String CURRENT = "CURRENT";
    private static final String NEXT = "NEXT";

    private final UtilityInvoiceService utilityInvoiceService;
    private final PropertyCostService propertyCostService;
    private final UnitCostService unitCostService;
    private final UtilityRuleService utilityRuleService;
    private final MeterService meterService;

    @Override
    @Transactional
    public SettlementResponseDto createSettlement(
            Long invoiceId, CreateSettlementRequestDto request) {
        UtilityInvoice utilityInvoice = utilityInvoiceService.getUtilityInvoiceEntity(invoiceId);
        return createCostsByInvoice(utilityInvoice, request.utilityTypes());
    }

    @Override
    @Transactional
    public SettlementResponseDto recalculate(RecalculateSettlementRequestDto request) {
        switch (request.source()) {
            case UTILITY_INVOICE -> {
                checkUtilityTypeListIsNotEmpty(request.utilityTypes());
                UtilityInvoice utilityInvoice =
                        utilityInvoiceService.getUtilityInvoiceEntity(request.id());
                List<PropertyCost> propertyCosts =
                        propertyCostService.getPropertyCostsEntities(utilityInvoice.getId());
                for (PropertyCost propertyCost : propertyCosts) {
                    unitCostService.deleteUnitCostsByPropertyCostId(propertyCost.getId());
                }
                propertyCostService.deletePropertyCostsByUtilityInvoiceId(utilityInvoice.getId());
                return createCostsByInvoice(utilityInvoice, request.utilityTypes());
            }
            case UTILITY_RULE -> {
                checkUtilityTypeListIsEmpty(request.utilityTypes());
                UtilityRule utilityRule = utilityRuleService.findUtilityRule(request.id());
                UtilityRuleHistory utilityRuleHistory = utilityRuleService.findUtilityRuleHistory(
                        request.id());
                if (utilityRule.getStartMonth().isAfter(utilityRuleHistory.getStartMonth())) {
                    utilityRuleService.checkUtilityRuleExistsInPast(utilityRule.getProperty().getId(),
                            utilityRule.getUtilityType(), utilityRuleHistory.getStartMonth(),
                            utilityRule.getStartMonth().minusMonths(1L));
                }
                if (utilityRule.getEndMonth() != null
                        && utilityRule.getEndMonth().isBefore(YearMonth.now())
                        && (utilityRuleHistory.getEndMonth() == null
                        || utilityRule.getEndMonth().isBefore(utilityRuleHistory.getEndMonth()))) {
                    utilityRuleService.checkUtilityRuleExistsInFuture(utilityRule.getProperty().getId(),
                            utilityRule.getUtilityType(), utilityRule.getEndMonth().plusMonths(1L),
                            YearMonth.now());
                }
                List<UnitCost> unitCosts = unitCostService.getUnitCostsEntitiesByUtilityRuleId(
                        utilityRuleHistory.getUtilityRuleId());
                unitCostService.deleteUnitCostsByUtilityRuleId(utilityRule.getId());
                Map<Long, PropertyCost> propertyCostsById = new HashMap<>();
                List<Long> unitCostIds = new ArrayList<>();
                for (UnitCost unitCost : unitCosts) {
                    PropertyCost propertyCost = unitCost.getPropertyCost();
                    propertyCostsById.put(propertyCost.getId(), propertyCost);
                }
                for (PropertyCost propertyCost : propertyCostsById.values()) {
                    List<UnitCost> newUnitCosts = unitCostService.createUnitCostEntities(
                            propertyCost, utilityRule.getUtilityType());
                    unitCostIds.addAll(newUnitCosts.stream().map(UnitCost::getId).toList());
                }
                List<Long> propertyCostIds = new ArrayList<>(propertyCostsById.keySet());
                return new SettlementResponseDto(propertyCostIds, unitCostIds);
            }
            case METER_READING -> {
                checkUtilityTypeListIsEmpty(request.utilityTypes());
                MeterReading meterReading = meterService.getReadingEntityById(request.id());
                Meter meter = meterReading.getMeter();
                YearMonth billingMonth = YearMonth.from(
                        meterReading.getReadingDate()).minusMonths(1);
                Unit unit = meter.getUnit();
                UtilityRule utilityRule;
                if (meter.getUnit() == null) {
                    utilityRule = utilityRuleService.findUtilityRuleByMeterData(
                            meter.getProperty().getId(), meter.getMeasurementType(),
                            meter.getPurpose(), false, billingMonth);
                } else {
                    utilityRule = utilityRuleService.findUtilityRuleByMeterData(
                            meter.getProperty().getId(), meter.getMeasurementType(),
                            meter.getPurpose(), true, billingMonth);
                }
                List<UtilityInvoice> invoices = utilityInvoiceService.getUtilityInvoicesEntities(
                        meter.getProperty().getId(), utilityRule.getInvoiceUtilityType(),
                        billingMonth);
                List<PropertyCost> propertyCosts = propertyCostService.getOrCreatePropertyCosts(
                        invoices, billingMonth);
                PropertyCost propertyCost = getSinglePropertyCost(
                        propertyCosts, utilityRule, billingMonth);
                if (meter.getUnit() == null) {
                    List<UnitCost> unitCosts = unitCostService
                            .findUnitCostEntitiesForPropertyMeterAndBillingMonth(
                                    meter.getId(), billingMonth);
                    Set<Long> propertyCostIds = new HashSet<>();
                    Set<Long> unitCostIds = new HashSet<>();
                    updateOrCreateUnitCosts(unitCosts, propertyCost, utilityRule.getUtilityType(),
                            CURRENT, propertyCostIds, unitCostIds);
                    List<UnitCost> nextUnitCosts = unitCostService
                            .findUnitCostEntitiesForPropertyMeterAndBillingMonth(
                                    meter.getId(), billingMonth.plusMonths(1L));
                    if (!nextUnitCosts.isEmpty()) {
                        updateOrCreateUnitCosts(nextUnitCosts, null, utilityRule.getUtilityType(),
                                NEXT, propertyCostIds, unitCostIds);
                    }
                    return new SettlementResponseDto(
                            List.copyOf(propertyCostIds), List.copyOf(unitCostIds));

                } else {
                    Set<Long> propertyCostIds = new HashSet<>();
                    List<Long> unitCostIds = new ArrayList<>();
                    Optional<UnitCost> optionalUnitCost = unitCostService
                            .findUnitCostEntityForUnitMeterAndBillingMonth(
                                    unit.getId(), meter.getId(), billingMonth);
                    if (optionalUnitCost.isEmpty()) {
                        UnitCost unitCost = unitCostService.createUnitCostEntity(
                                unit.getId(), propertyCost.getId(), utilityRule.getUtilityType());
                        propertyCostIds.add(unitCost.getPropertyCost().getId());
                        unitCostIds.add(unitCost.getId());
                    } else {
                        unitCostService.deleteUnitCostById(optionalUnitCost.get().getId());
                        UnitCost updatedUnitCost = unitCostService.createUnitCostEntity(
                                unit.getId(), propertyCost.getId(), utilityRule.getUtilityType());
                        propertyCostIds.add(updatedUnitCost.getPropertyCost().getId());
                        unitCostIds.add(updatedUnitCost.getId());
                    }
                    Optional<UnitCost> optionalNextUnitCost = unitCostService
                            .findUnitCostEntityForUnitMeterAndBillingMonth(
                                    unit.getId(), meter.getId(), billingMonth.plusMonths(1L));
                    if (optionalNextUnitCost.isPresent()) {
                        UnitCost nextUnitCost = optionalNextUnitCost.get();
                        unitCostService.deleteUnitCostById(nextUnitCost.getId());
                        UnitCost updatedNextUnitCost = unitCostService.createUnitCostEntity(
                                unit.getId(), nextUnitCost.getPropertyCost().getId(),
                                utilityRule.getUtilityType());
                        propertyCostIds.add(updatedNextUnitCost.getPropertyCost().getId());
                        unitCostIds.add(updatedNextUnitCost.getId());
                    }
                    return new SettlementResponseDto(List.copyOf(propertyCostIds), unitCostIds);
                }
            }
            default -> throw new InvalidSettlementDataException("Unsupported recalculation source: "
                    + request.source());
        }
    }

    private void checkUtilityTypeListIsNotEmpty(List<UtilityType> utilityTypes) {
        if (utilityTypes == null || utilityTypes.isEmpty()) {
            throw new InvalidSettlementDataException("Utility types can't be null or empty");
        }
    }

    private void checkUtilityTypeListIsEmpty(List<UtilityType> utilityTypes) {
        if (utilityTypes != null && !utilityTypes.isEmpty()) {
            throw new InvalidSettlementDataException("Utility types has to be null or empty");
        }
    }

    private SettlementResponseDto createCostsByInvoice(
            UtilityInvoice utilityInvoice, List<UtilityType> utilityTypes) {
        List<PropertyCost> propertyCosts = propertyCostService.createPropertyCosts(utilityInvoice);
        List<Long> propertyCostIds = new ArrayList<>();
        List<Long> unitCostIds = new ArrayList<>();
        for (PropertyCost propertyCost : propertyCosts) {
            for (UtilityType utilityType : utilityTypes) {
                List<UnitCost> unitCosts = unitCostService.createUnitCostEntities(
                        propertyCost, utilityType);
                unitCostIds.addAll(unitCosts.stream().map(UnitCost::getId).toList());
            }
            propertyCostIds.add(propertyCost.getId());
        }
        return new SettlementResponseDto(propertyCostIds, unitCostIds);
    }

    private PropertyCost getSinglePropertyCost(
            List<PropertyCost> propertyCosts, UtilityRule utilityRule, YearMonth billingMonth) {
        if (propertyCosts.isEmpty()) {
            throw new EntityNotFoundException("Property cost doesn't exist for property with id "
                    + utilityRule.getProperty().getId() + ", utility type "
                    + utilityRule.getUtilityType().name().toLowerCase().replace('_', ' ')
                    + ", billing month " + billingMonth + " doesn't exist in database");
        }
        if (propertyCosts.size() > 1) {
            throw new InvalidSettlementDataException("More than one property cost found for "
                    + "property with id " + utilityRule.getProperty().getId() + ", utility type "
                    + utilityRule.getUtilityType().name().toLowerCase().replace('_', ' ')
                    + ", billing month " + billingMonth);
        }
        return propertyCosts.getFirst();
    }

    private void updateOrCreateUnitCosts(List<UnitCost> unitCosts, PropertyCost propertyCost,
                                         UtilityType utilityType, String month,
                                         Set<Long> propertyCostIds, Set<Long> unitCostIds) {
        if (unitCosts.isEmpty() && month.equals(CURRENT)) {
            List<UnitCost> unitCostList = unitCostService.createUnitCostEntities(
                    propertyCost, utilityType);
            for (UnitCost unitCost : unitCostList) {
                propertyCostIds.add(unitCost.getPropertyCost().getId());
                unitCostIds.add(unitCost.getId());
            }
        } else {
            PropertyCost propertyCostFromList = unitCosts.getFirst().getPropertyCost();
            Long meterId = unitCosts.getFirst().getMeter().getId();
            unitCostService.deleteUnitCostsByPropertyCostIdAndMeterId(
                    propertyCostFromList.getId(), meterId);
            List<UnitCost> updatedUnitCosts = unitCostService.createUnitCostEntities(
                    propertyCostFromList, utilityType);
            propertyCostIds.add(propertyCostFromList.getId());
            unitCostIds.addAll(updatedUnitCosts.stream()
                    .map(UnitCost::getId).toList());
        }
    }
}

