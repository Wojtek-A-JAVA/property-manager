package property.manager.specification;

import jakarta.persistence.criteria.Predicate;
import java.time.YearMonth;
import org.springframework.data.jpa.domain.Specification;
import property.manager.model.MeterReading;

public class MeterReadingSpecification {
    public static Specification<MeterReading> hasMeterId(Long meterId) {
        return (root, query, criteriaBuilder) -> {
            if (meterId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("meter").get("id"), meterId);
        };
    }

    public static Specification<MeterReading> isWithinPeriod(
            YearMonth fromMonth, YearMonth toMonth) {
        return (root, query, criteriaBuilder) -> {
            if (fromMonth == null && toMonth == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (fromMonth != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                        root.get("readingMonth"), fromMonth)
                );
            }
            if (toMonth != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                        root.get("readingMonth"), toMonth)
                );
            }
            return predicate;
        };
    }
}
