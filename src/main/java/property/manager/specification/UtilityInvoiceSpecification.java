package property.manager.specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import property.manager.model.UtilityInvoice;
import property.manager.model.UtilityType;

public class UtilityInvoiceSpecification {
    public static Specification<UtilityInvoice> hasPropertyId(Long propertyId) {
        return (root, query, criteriaBuilder) -> {
            if (propertyId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("property").get("id"), propertyId);
        };
    }

    public static Specification<UtilityInvoice> hasUtilityType(UtilityType utilityType) {
        return (root, query, criteriaBuilder) -> {
            if (utilityType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("utilityType"), utilityType);
        };
    }

    public static Specification<UtilityInvoice> hasSupplierTaxId(String supplierTaxId) {
        return (root, query, criteriaBuilder) -> {
            if (supplierTaxId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("supplier").get("taxId"), supplierTaxId);
        };
    }

    public static Specification<UtilityInvoice> hasInvoiceDateBetween(
            LocalDate invoiceDateFrom, LocalDate invoiceDateTo) {
        return (root, query, criteriaBuilder) -> {
            if (invoiceDateFrom == null && invoiceDateTo == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (invoiceDateFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                        root.get("invoiceDate"), invoiceDateFrom)
                );
            }
            if (invoiceDateTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                        root.get("invoiceDate"), invoiceDateTo)
                );
            }
            return predicate;
        };
    }

    public static Specification<UtilityInvoice> hasBillingPeriodOverlap(
            LocalDate billingFrom, LocalDate billingTo) {
        return (root, query, criteriaBuilder) -> {
            if (billingFrom == null && billingTo == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (billingFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                        root.get("billingFrom"), billingFrom)
                );
            }
            if (billingTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                        root.get("billingTo"), billingTo)
                );
            }
            return predicate;
        };
    }

    public static Specification<UtilityInvoice> hasAmountBetween(
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
}
