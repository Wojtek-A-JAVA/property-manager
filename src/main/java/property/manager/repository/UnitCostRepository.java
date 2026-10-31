package property.manager.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import property.manager.model.UnitCost;
import property.manager.model.UtilityType;

@Repository
public interface UnitCostRepository extends JpaRepository<UnitCost, Long>,
        JpaSpecificationExecutor<UnitCost> {

    boolean existsByUnitIdAndPropertyCostIdAndUtilityRuleUtilityType(
            Long unitId, Long propertyCostId, UtilityType utilityType);

    boolean existsByUnitIdAndPropertyCostIdAndUtilityRuleUtilityTypeAndIdNot(
            Long unitId, Long propertyCostId, UtilityType utilityType, Long unitCostId);

    void deleteAllByPropertyCost_Id(Long id);

    void deleteAllByUtilityRule_Id(Long utilityRuleId);

    void deleteAllByPropertyCostIdAndMeterId(Long propertyCostId, Long meterId);

    List<UnitCost> findAllByUtilityRule_Id(Long utilityRuleId);

    Optional<UnitCost> findByUnitIdAndPropertyCostIdAndUtilityRuleId(
            Long unitId, Long propertyCostId, Long utilityRuleId);

    Optional<UnitCost> findByUnitIdAndMeterIdAndBillingMonth(
            Long unitId, Long meterId, YearMonth billingMonth);

    List<UnitCost> findAllByMeterIdAndBillingMonth(Long meterId, YearMonth billingMonth);
}
