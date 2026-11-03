package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "12. Settlements")
@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "Create settlement",
            description = "Creates settlement costs for the specified utility invoice")
    @PostMapping("/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public SettlementResponseDto createSettlement(
            @PathVariable Long invoiceId, @RequestBody @Valid CreateSettlementRequestDto request) {
        return settlementService.createSettlement(invoiceId, request);
    }

    @Operation(summary = "Recalculate settlement", description =
                    "Recalculates settlement costs based on the selected recalculation source")
    @PatchMapping("/recalculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public SettlementResponseDto recalculate(
            @Valid @RequestBody RecalculateSettlementRequestDto request) {
        return settlementService.recalculate(request);
    }
}
