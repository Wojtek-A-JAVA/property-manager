package property.manager.specification;

import org.springframework.data.jpa.domain.Specification;
import property.manager.model.MeasurementType;
import property.manager.model.Meter;
import property.manager.model.MeterPurpose;

public class MeterSpecification {
    public static Specification<Meter> hasPropertyId(Long propertyId) {
        return (root, query, criteriaBuilder) -> {
            if (propertyId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("property").get("id"), propertyId);
        };
    }

    public static Specification<Meter> hasUnitId(Long unitId) {
        return (root, query, criteriaBuilder) -> {
            if (unitId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("unit").get("id"), unitId);
        };
    }

    public static Specification<Meter> hasMeasurementType(MeasurementType measurementType) {
        return (root, query, criteriaBuilder) -> {
            if (measurementType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("measurementType"), measurementType);
        };
    }

    public static Specification<Meter> hasPurpose(MeterPurpose purpose) {
        return (root, query, criteriaBuilder) -> {
            if (purpose == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("purpose"), purpose);
        };
    }

    public static Specification<Meter> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (active == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("active"), active);
        };
    }
}
