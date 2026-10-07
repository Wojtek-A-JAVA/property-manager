package property.manager.controler;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/utility-rules")
@RequiredArgsConstructor
public class UtilityRuleController {

    private final UtilityRuleService utilityRuleService;

    @PostMapping
    UtilityRuleResponseDto createUtilityRule(
            @Valid @RequestBody CreateUtilityRuleRequestDto request) {
        return utilityRuleService.createUtilityRule(request);
    }

    @GetMapping
    public List<UtilityRuleResponseDto> getUtilityRules(
            @RequestParam Long propertyId,
            @RequestParam(required = false) UtilityType utilityType) {
        return utilityRuleService.getUtilityRules(propertyId, utilityType);
    }

    @GetMapping("/{id}")
    public UtilityRuleResponseDto getUtilityRule(@PathVariable Long id) {
        return utilityRuleService.getUtilityRule(id);
    }

    @PatchMapping("/{id}/end-month")
    public UtilityRuleResponseDto setUtilityRuleEndMonth(
            @PathVariable Long id,
            @RequestBody @Valid UtilityRuleEndMonthRequestDto request) {
        return utilityRuleService.setUtilityRuleEndMonth(id, request);
    }

    @PutMapping("/{id}")
    public UtilityRuleResponseDto updateUtilityRule(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUtilityRuleRequestDto request) {
        return utilityRuleService.updateUtilityRule(id, request);
    }
}
