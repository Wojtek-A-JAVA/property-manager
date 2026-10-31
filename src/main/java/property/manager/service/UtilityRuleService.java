package property.manager.service;

import java.time.YearMonth;
import java.util.List;
import property.manager.dto.utility.rule.CreateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UpdateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UtilityRuleEndMonthRequestDto;
import property.manager.dto.utility.rule.UtilityRuleResponseDto;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;
import property.manager.model.UtilityRule;
import property.manager.model.UtilityRuleHistory;
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

    UtilityRule findUtilityRule(Long id);

    UtilityRuleHistory findUtilityRuleHistory(Long utilityRuleId);

    void checkUtilityRuleExistsInPast(
            Long propertyId, UtilityType utilityType, YearMonth startMonth, YearMonth endMonth);

    void checkUtilityRuleExistsInFuture(
            Long propertyId, UtilityType utilityType, YearMonth startMonth, YearMonth endMonth);

    UtilityRule findUtilityRuleByMeterData(
            Long propertyId, MeasurementType measurementType, MeterPurpose meterPurpose,
            boolean unitMeterRequired, YearMonth billingMonth);
}
