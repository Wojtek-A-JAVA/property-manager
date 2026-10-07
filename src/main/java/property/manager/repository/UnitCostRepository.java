package property.manager.repository;

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
}
