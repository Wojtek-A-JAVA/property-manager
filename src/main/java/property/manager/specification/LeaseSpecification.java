package property.manager.specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import property.manager.model.Lease;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;

public class LeaseSpecification {
    public static Specification<Lease> hasStatus(LeaseStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("leaseStatus"), status);
        };
    }

    public static Specification<Lease> hasTenantId(Long tenantId) {
        return (root, query, criteriaBuilder) -> {
            if (tenantId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("tenant").get("id"), tenantId);
        };
    }

    public static Specification<Lease> hasUnitId(Long unitId) {
        return (root, query, criteriaBuilder) -> {
            if (unitId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("unit").get("id"), unitId);
        };
    }

    public static Specification<Lease> hasVatRate(VatRate vatRate) {
        return (root, query, criteriaBuilder) -> {
            if (vatRate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("vatRate"), vatRate);
        };
    }

    public static Specification<Lease> overlapsPeriod(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (toDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                        root.get("startDate"), toDate)
                );
            }
            if (fromDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get("endDate")),
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("endDate"), fromDate))
                );
            }
            return predicate;
        };
    }

    public static Specification<Lease> isOpenEnded(Boolean openEnded) {
        return (root, query, criteriaBuilder) -> {
            if (openEnded == null) {
                return criteriaBuilder.conjunction();
            }
            if (openEnded) {
                return criteriaBuilder.isNull(root.get("endDate"));
            }
            return criteriaBuilder.isNotNull(root.get("endDate"));
        };
    }
}
