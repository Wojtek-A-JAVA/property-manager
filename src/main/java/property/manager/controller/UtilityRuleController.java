package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.utility.rule.CreateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UpdateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UtilityRuleEndMonthRequestDto;
import property.manager.dto.utility.rule.UtilityRuleResponseDto;
import property.manager.model.UtilityType;
import property.manager.service.UtilityRuleService;

@Tag(name = "08. Utility Rules")
@RestController
@RequestMapping("/api/utility-rules")
@RequiredArgsConstructor
public class UtilityRuleController {

    private final UtilityRuleService utilityRuleService;

    @Operation(summary = "Create utility rule",
            description = "Creates a new utility allocation rule")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    UtilityRuleResponseDto createUtilityRule(
            @Valid @RequestBody CreateUtilityRuleRequestDto request) {
        return utilityRuleService.createUtilityRule(request);
    }

    @Operation(summary = "Get utility rules", description =
            "Returns utility rules for the specified property and optional utility type")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<UtilityRuleResponseDto> getUtilityRules(
            @RequestParam Long propertyId,
            @RequestParam(required = false) UtilityType utilityType) {
        return utilityRuleService.getUtilityRules(propertyId, utilityType);
    }

    @Operation(summary = "Get utility rule",
            description = "Returns a utility rule by its id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public UtilityRuleResponseDto getUtilityRule(@PathVariable Long id) {
        return utilityRuleService.getUtilityRule(id);
    }

    @Operation(summary = "Set utility rule end month", description =
            "Sets the end month for an existing utility rule."
                    + " This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}/end-month")
    @PreAuthorize("hasRole('ADMIN')")
    public UtilityRuleResponseDto setUtilityRuleEndMonth(
            @PathVariable Long id,
            @RequestBody @Valid UtilityRuleEndMonthRequestDto request) {
        return utilityRuleService.setUtilityRuleEndMonth(id, request);
    }

    @Operation(summary = "Update utility rule", description =
            "Updates an existing utility rule. This operation is restricted to ADMIN users.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UtilityRuleResponseDto updateUtilityRule(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUtilityRuleRequestDto request) {
        return utilityRuleService.updateUtilityRule(id, request);
    }
}
