package property.manager.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import property.manager.model.Lease;
import property.manager.model.LeaseStatus;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long>,
        JpaSpecificationExecutor<Lease> {

    @Query("""
            SELECT l
            FROM Lease l
            WHERE l.unit.id = :unitId
              AND l.leaseStatus = :status
              AND (l.endDate IS NULL OR l.endDate >= :startDate)
            """)
    Optional<Lease> findConflictingLease(Long unitId, LeaseStatus status, LocalDate startDate);

    Optional<Lease> findById(Long id);
}
