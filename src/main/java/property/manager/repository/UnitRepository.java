package property.manager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import property.manager.model.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Optional<Unit> findByPropertyIdAndUnitNumber(Long propertyId, String unitNumber);

    Optional<Unit> findById(Long id);
}
