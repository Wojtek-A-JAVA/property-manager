package property.manager.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import property.manager.model.MeterReading;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long>,
        JpaSpecificationExecutor<MeterReading> {

    boolean existsByMeterIdAndReadingDate(Long meterId, LocalDate readingDate);

    Optional<MeterReading> findById(Long id);
}
