package property.manager.specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import property.manager.model.PropertyCost;
import property.manager.model.UtilityType;

public class PropertyCostSpecification {
    public static Specification<PropertyCost> hasPropertyId(Long propertyId) {
        return (root, query, criteriaBuilder) -> {
            if (propertyId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("property").get("id"), propertyId);
        };
    }

    public static Specification<PropertyCost> hasAmountBetween(
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

    public static Specification<PropertyCost> hasBillingMonthBetween(
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

    public static Specification<PropertyCost> hasUtilityTypes(List<UtilityType> utilityTypes) {
        return (root, query, criteriaBuilder) -> {
            if (utilityTypes == null || utilityTypes.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("utilityInvoice").get("utilityType").in(utilityTypes);
        };
    }

    public static Specification<PropertyCost> hasNotes(Boolean hasNotes) {
        return (root, query, criteriaBuilder) -> {
            if (hasNotes == null) {
                return criteriaBuilder.conjunction();
            }
            if (hasNotes) {
                return criteriaBuilder.isNotNull(root.get("notes"));
            }
            return criteriaBuilder.isNull(root.get("notes"));
        };
    }
}
