package property.manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.settlement.CreateSettlementRequestDto;
import property.manager.dto.settlement.RecalculateSettlementRequestDto;
import property.manager.dto.settlement.SettlementResponseDto;
import property.manager.service.SettlementService;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping("/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public SettlementResponseDto createSettlement(
            @PathVariable Long invoiceId, @RequestBody @Valid CreateSettlementRequestDto request) {
        return settlementService.createSettlement(invoiceId, request);
    }

    @PatchMapping("/recalculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public SettlementResponseDto recalculate(
            @Valid @RequestBody RecalculateSettlementRequestDto request) {
        return settlementService.recalculate(request);
    }
}
