package property.manager.repository;

import java.time.YearMonth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import property.manager.model.MeterReading;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long>,
        JpaSpecificationExecutor<MeterReading> {

    boolean existsByMeterIdAndReadingMonth(Long meterId, YearMonth readingMonth);

    Optional<MeterReading> findByMeterIdAndReadingMonth(Long meterId, YearMonth readingMonth);

    Optional<MeterReading> findFirstByMeterIdAndReadingMonthBeforeOrderByReadingMonthDesc(
            Long meterId, YearMonth readingMonth);

    Optional<MeterReading> findFirstByMeterIdAndReadingMonthAfterOrderByReadingMonthAsc(
            Long meterId, YearMonth readingMonth);
}
