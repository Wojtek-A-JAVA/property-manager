package property.manager.service;

import java.util.List;
import property.manager.dto.utility.rule.CreateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UpdateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UtilityRuleEndMonthRequestDto;
import property.manager.dto.utility.rule.UtilityRuleResponseDto;
import property.manager.model.UtilityType;

public interface UtilityRuleService {
    UtilityRuleResponseDto createUtilityRule(
            CreateUtilityRuleRequestDto request);

    List<UtilityRuleResponseDto> getUtilityRules(
            Long propertyId, UtilityType utilityType);

    UtilityRuleResponseDto getUtilityRule(Long id);

    UtilityRuleResponseDto setUtilityRuleEndMonth(
            Long id, UtilityRuleEndMonthRequestDto request);

    UtilityRuleResponseDto updateUtilityRule(
            Long id, UpdateUtilityRuleRequestDto request);
}
