package property.manager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import property.manager.model.User;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
}
