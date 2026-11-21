package property.manager.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT MAX(mr.readingMonth) FROM MeterReading mr")
    Optional<YearMonth> findMaxReadingMonth();

    List<MeterReading> findByReadingMonth(YearMonth yearMonth);
}
