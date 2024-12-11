package i.service;

import i.dto.RoleDto;
import i.model.Role;
import i.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    /**
     * Saves a role if it doesn't already exist, or retrieves it if it does.
     * Logs the creation or retrieval of the role.
     *
     * @param dto The RoleDto containing the role information to be saved or retrieved.
     * @return The RoleDto with the details of the saved or retrieved role.
     */
    public RoleDto save(RoleDto dto) {
        log.debug("Attempting to save or retrieve role with name: {}", dto.getName());

        // Check if the role exists, if not, create and save it
        Role role = repository.findByName(dto.getName())
                .orElseGet(() -> {
                    log.info("Role '{}' not found, creating a new role.", dto.getName());
                    return repository.save(new Role(dto.getName()));
                });

        log.info("Role '{}' processed. ID: {}", role.getName(), role.getId());

        // Return the RoleDto with the role details
        return new RoleDto(role.getId(), role.getName());
    }
}
