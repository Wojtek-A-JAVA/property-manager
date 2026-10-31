package property.manager.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import property.manager.model.UtilityInvoice;
import property.manager.model.UtilityType;

@Repository
public interface UtilityInvoiceRepository extends JpaRepository<UtilityInvoice, Long>,
        JpaSpecificationExecutor<UtilityInvoice> {

    boolean existsBySupplierIdAndInvoiceNumber(Long supplierId, String invoiceNumber);

    @Query("""
              SELECT i
              FROM UtilityInvoice i
              WHERE i.property.id = :propertyId
              AND i.utilityType = :utilityType
              AND i.billingFrom <= :monthEnd
              AND i.billingTo >= :monthStart
            """)
    List<UtilityInvoice> findAllByPropertyIdAndUtilityTypeAndDate(
            Long propertyId, UtilityType utilityType, LocalDate monthStart, LocalDate monthEnd);
}
