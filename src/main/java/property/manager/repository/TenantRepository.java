package property.manager.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import property.manager.model.Tenant;
import property.manager.model.TenantType;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsByTaxId(String taxId);

    boolean existsByCompanyName(String companyName);

    boolean existsByPesel(String pesel);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    Optional<Tenant> findById(Long id);

    boolean existsByPeselAndIdNot(String pesel, Long id);

    boolean existsByTaxIdAndIdNot(String taxId, Long id);

    List<Tenant> findByActive(Boolean active);

    List<Tenant> findByType(TenantType type);

    List<Tenant> findByActiveAndType(Boolean active, TenantType type);
}
