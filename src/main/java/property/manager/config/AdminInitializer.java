package property.manager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import property.manager.model.Role;
import property.manager.model.RoleName;
import property.manager.model.User;
import property.manager.repository.RoleRepository;
import property.manager.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${demo.admin.username}")
    private String demoAdminUsername;
    @Value("${demo.admin.password}")
    private String demoAdminPassword;

    @Override
    public void run(String... args) {
        createUserIfNotExists(adminUsername,adminPassword, RoleName.ADMIN);
        createUserIfNotExists(demoAdminUsername, demoAdminPassword, RoleName.DEMO_ADMIN);
    }

    private void createUserIfNotExists(String username, String password, RoleName roleName) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalStateException(roleName
                        + " role doesn't exist in database"));
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
    }
}
