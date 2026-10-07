package property.manager.specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import property.manager.model.AllocationMethod;
import property.manager.model.UnitCost;
import property.manager.model.UtilityType;

public class UnitCostSpecification {
    public static Specification<UnitCost> hasPropertyId(Long propertyId) {
        return (root, query, criteriaBuilder) -> {
            if (propertyId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("propertyCost").get("property").get("id"),
                    propertyId);
        };
    }

    public static Specification<UnitCost> hasUnitId(Long unitId) {
        return (root, query, criteriaBuilder) -> {
            if (unitId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("unit").get("id"), unitId);
        };
    }

    public static Specification<UnitCost> hasBillingMonthBetween(
            YearMonth billingMonthFrom, YearMonth billingMonthTo) {
        return (root, query, criteriaBuilder) -> {
            if (billingMonthFrom == null && billingMonthTo == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (billingMonthFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                        root.get("billingMonth"), billingMonthFrom)
                );
            }
            if (billingMonthTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                        root.get("billingMonth"), billingMonthTo)
                );
            }
            return predicate;
        };
    }

    public static Specification<UnitCost> hasAmountBetween(
            BigDecimal amountFrom, BigDecimal amountTo) {
        return (root, query, criteriaBuilder) -> {
            if (amountFrom == null && amountTo == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (amountFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                        root.get("amount"), amountFrom)
                );
            }
            if (amountTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                        root.get("amount"), amountTo)
                );
            }
            return predicate;
        };
    }

    public static Specification<UnitCost> hasUtilityTypes(List<UtilityType> utilityTypes) {
        return (root, query, criteriaBuilder) -> {
            if (utilityTypes == null || utilityTypes.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("utilityRule").get("utilityType").in(utilityTypes);
        };
    }

    public static Specification<UnitCost> hasAllocationMethod(AllocationMethod allocationMethod) {
        return (root, query, criteriaBuilder) -> {
            if (allocationMethod == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    root.get("utilityRule").get("allocationMethod"), allocationMethod);
        };
    }

    public static Specification<UnitCost> hasMeterId(Long meterId) {
        return (root, query, criteriaBuilder) -> {
            if (meterId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("meter").get("id"), meterId);
        };
    }
}
