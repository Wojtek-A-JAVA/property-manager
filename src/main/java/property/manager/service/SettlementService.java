package property.manager.service;

import property.manager.dto.settlement.CreateSettlementRequestDto;
import property.manager.dto.settlement.RecalculateSettlementRequestDto;
import property.manager.dto.settlement.SettlementResponseDto;

public interface SettlementService {
    SettlementResponseDto createSettlement(Long invoiceId, CreateSettlementRequestDto request);

    SettlementResponseDto recalculate(RecalculateSettlementRequestDto request);
}
