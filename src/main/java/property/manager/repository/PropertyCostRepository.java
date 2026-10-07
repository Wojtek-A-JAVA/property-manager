package property.manager.repository;

import java.time.YearMonth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import property.manager.model.PropertyCost;
import property.manager.model.UtilityType;

@Repository
public interface PropertyCostRepository extends JpaRepository<PropertyCost, Long>,
        JpaSpecificationExecutor<PropertyCost> {

    boolean existsByPropertyIdAndUtilityInvoiceUtilityTypeAndBillingMonthBetween(
            Long propertyId, UtilityType utilityType, YearMonth startMonth, YearMonth endMonth);

    List<PropertyCost> findAllByUtilityInvoice_Id(Long id);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM PropertyCost c
            WHERE c.property.id = :propertyId
              AND c.utilityInvoice.utilityType = :utilityType
              AND c.billingMonth BETWEEN :startMonth AND :endMonth
              AND c.utilityInvoice.id <> :utilityInvoiceId
            """)
    boolean existsConflictingCost(Long propertyId, UtilityType utilityType, YearMonth startMonth,
                                  YearMonth endMonth, Long utilityInvoiceId);
}
