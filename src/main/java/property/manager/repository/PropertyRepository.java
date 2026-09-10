package property.manager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import property.manager.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    boolean existsByNameAndAddress(String name, String address);

    Optional<Property> findByNameAndAddress(String name, String address);

    Optional<Property> findById(Long id);
}
