package property.manager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import property.manager.model.MeasurementType;
import property.manager.model.Meter;
import property.manager.model.MeterPurpose;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long>,
        JpaSpecificationExecutor<Meter> {

    boolean existsByUnitIdAndMeasurementTypeAndPurposeAndActiveTrue(
            Long unitId, MeasurementType measurementType, MeterPurpose purpose);

    boolean existsByPropertyIdAndUnitIsNullAndMeasurementTypeAndPurposeAndActiveTrue(
            Long propertyId, MeasurementType measurementType, MeterPurpose purpose);

    Optional<Meter> findById(Long id);
}
