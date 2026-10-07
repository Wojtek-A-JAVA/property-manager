package property.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property.manager.dto.settlement.CreateSettlementRequestDto;
import property.manager.dto.settlement.SettlementResponseDto;
import property.manager.model.PropertyCost;
import property.manager.model.UnitCost;
import property.manager.model.UtilityInvoice;
import property.manager.model.UtilityType;
import property.manager.service.PropertyCostService;
import property.manager.service.SettlementService;
import property.manager.service.UnitCostService;
import property.manager.service.UtilityInvoiceService;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final UtilityInvoiceService utilityInvoiceService;
    private final PropertyCostService propertyCostService;
    private final UnitCostService unitCostService;

    @Override
    @Transactional
    public SettlementResponseDto createSettlement(
            Long invoiceId, CreateSettlementRequestDto request) {
        UtilityInvoice utilityInvoice = utilityInvoiceService.getUtilityInvoiceEntity(invoiceId);
        List<PropertyCost> propertyCosts = propertyCostService.createPropertyCosts(utilityInvoice);
        List<Long> propertyCostIds = new ArrayList<>();
        List<Long> unitCostIds = new ArrayList<>();
        for (PropertyCost propertyCost : propertyCosts) {
            for (UtilityType utilityType : request.utilityTypes()) {
                List<UnitCost> unitCosts = unitCostService.createUnitCostEntities(
                        propertyCost, utilityType);
                unitCostIds.addAll(unitCosts.stream().map(UnitCost::getId).toList());
            }
            propertyCostIds.add(propertyCost.getId());
        }
        return new SettlementResponseDto(utilityInvoice.getId(), propertyCostIds, unitCostIds);
    }
}
