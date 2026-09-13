package property.manager.specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
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

    public static Specification<MeterReading> isWithinPeriod(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (fromDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                        root.get("readingDate"), fromDate)
                );
            }
            if (toDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                        root.get("readingDate"), toDate)
                );
            }
            return predicate;
        };
    }
}
