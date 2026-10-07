package property.manager.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import property.manager.model.UtilityRule;
import property.manager.model.UtilityType;

@Repository
public interface UtilityRuleRepository extends JpaRepository<UtilityRule, Long> {

    @Query("""
            SELECT r
            FROM UtilityRule r
            WHERE r.property.id = :propertyId
              AND r.utilityType = :utilityType
              AND (:endMonth IS NULL OR r.startMonth <= :endMonth)
              AND (r.endMonth IS NULL OR r.endMonth >= :startMonth)
              AND r.active = true
            """)
    List<UtilityRule> findOverlappingRules(
            Long propertyId, UtilityType utilityType, YearMonth startMonth, YearMonth endMonth);

    List<UtilityRule> findAllByPropertyId(Long propertyId);

    List<UtilityRule> findAllByPropertyIdAndUtilityType(
            Long propertyId, UtilityType utilityType);

    @Query("""
            SELECT r
            FROM UtilityRule r
            WHERE r.property.id = :propertyId
              AND r.utilityType = :utilityType
              AND (:endMonth IS NULL OR r.startMonth <= :endMonth)
              AND (r.endMonth IS NULL OR r.endMonth >= :startMonth)
              AND r.active = true
              AND r.id <> :id
            """)
    List<UtilityRule> findOverlappingRulesExcludingId(
            Long propertyId, UtilityType utilityType, YearMonth startMonth, YearMonth endMonth,
            Long id);

    @Query("""
            SELECT r
            FROM UtilityRule r
            WHERE r.property.id = :propertyId
              AND r.utilityType = :utilityType
              AND r.startMonth <= :billingMonth
              AND (r.endMonth IS NULL OR r.endMonth >= :billingMonth)
              AND r.active = true
            """)
    Optional<UtilityRule> findByPropertyIdAndUtilityTypeAndBillingMonth(
            Long propertyId, UtilityType utilityType, YearMonth billingMonth);
}
