package property.manager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import property.manager.model.Role;
import property.manager.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);
}
