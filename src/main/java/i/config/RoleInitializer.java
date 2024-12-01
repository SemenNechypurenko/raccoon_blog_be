package i.config;

import i.model.Role;
import i.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes default roles in the system.
 */
@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeRole("ROLE_USER");
        initializeRole("ROLE_ADMIN");
    }

    /**
     * Creates a role in the database if it does not already exist.
     *
     * @param roleName the name of the role to be created
     */
    private void initializeRole(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            System.out.println("Role created: " + roleName);
        }
    }
}

