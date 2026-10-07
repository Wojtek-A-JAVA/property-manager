package property.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import property.manager.model.UtilityInvoice;

@Repository
public interface UtilityInvoiceRepository extends JpaRepository<UtilityInvoice, Long>,
        JpaSpecificationExecutor<UtilityInvoice> {

    boolean existsBySupplierIdAndInvoiceNumber(Long supplierId, String invoiceNumber);
}
