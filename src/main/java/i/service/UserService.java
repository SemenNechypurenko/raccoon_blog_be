package i.service;

import i.dto.RoleDto;
import i.dto.UserCreateRequestDto;
import i.dto.UserDto;
import i.model.Role;
import i.model.User;
import i.repository.RoleRepository;
import i.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    /**
     * Saves a new user and sends a confirmation email with a token.
     *
     * @param userCreateRequestDto The user details for the new account.
     * @return The UserDto containing the saved user's information.
     */
    public UserDto save(UserCreateRequestDto userCreateRequestDto) {
        log.debug("Attempting to save a new user: {}", userCreateRequestDto.getUsername());

        // Validate if the username and email are unique
        validateUniqueUser(userCreateRequestDto.getUsername(), userCreateRequestDto.getEmail());

        // Convert the request DTO to a User entity
        User user = convertToEntity(userCreateRequestDto);

        final String confirmationToken = UUID.randomUUID().toString();
        user.setConfirmationToken(confirmationToken);

        // Save the user to the repository
        user = repository.save(user);

        // Send confirmation email to the user
        emailService.sendConfirmationEmail(user.getEmail(), confirmationToken);
        log.info("Confirmation email sent to user: {}", user.getEmail());

        // Return the UserDto with the user's details
        return convertFromEntity(user);
    }

    /**
     * Converts the UserCreateRequestDto to a User entity.
     */
    public User convertToEntity(UserCreateRequestDto userCreateRequestDto) {
        log.debug("Converting UserCreateRequestDto to User entity.");

        // Map the basic fields using ModelMapper
        User user = modelMapper.map(userCreateRequestDto, User.class);

        // Set and validate roles
        Set<Role> roles = getValidatedRoles(userCreateRequestDto.getRoles());
        user.setRoles(roles);
        return user;
    }

    /**
     * Validates that the username and email are unique.
     * Throws an exception if either already exists in the system.
     */
    private void validateUniqueUser(String username, String email) {
        log.debug("Validating uniqueness for username: {} and email: {}", username, email);

        Optional<User> userByUsername = repository.findByUsername(username);
        if (userByUsername.isPresent()) {
            log.error("Username already exists: {}", username);
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        Optional<User> userByEmail = repository.findByEmail(email);
        if (userByEmail.isPresent()) {
            log.error("Email already exists: {}", email);
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    /**
     * Converts the User entity to a UserDto.
     */
    public UserDto convertFromEntity(User user) {
        log.debug("Converting User entity to UserDto.");

        // Map the basic fields using ModelMapper
        UserDto responseDto = modelMapper.map(user, UserDto.class);

        // Set the roles for the response
        Set<RoleDto> roleDtos = user.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        responseDto.setRoles(roleDtos);
        return responseDto;
    }

    /**
     * Validates the existence of roles from the DTO and returns a Set<Role>.
     */
    private Set<Role> getValidatedRoles(Set<RoleDto> roleDtos) {
        log.debug("Validating roles for user creation.");

        Set<Role> roles = new HashSet<>();

        // If roles are not specified, add the default "ROLE_USER"
        if (roleDtos.isEmpty()) {
            roles.add(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role 'ROLE_USER' not found")));
        } else {
            // Validate each role in the database
            for (RoleDto roleDto : roleDtos) {
                Role role = roleRepository.findByName(roleDto.getName())
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleDto.getName()));
                roles.add(role);
            }
        }
        return roles;
    }

    /**
     * Confirms the user's email by verifying the confirmation token.
     */
    public void confirmEmail(String token) {
        log.debug("Confirming email for token: {}", token);

        User user = repository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Confirmation token not found"));

        // Confirm email and clear the token
        user.setEmailVerified(true);
        user.setConfirmationToken(null);  // Token is no longer needed after confirmation

        // Save the updated user entity
        repository.save(user);
        log.info("Email confirmed for user: {}", user.getEmail());
    }

    /**
     * Retrieves users whose username contains the given substring.
     */
    public List<UserDto> getUsernamesListBySubstring(String substring) {
        log.debug("Searching for usernames containing the substring: {}", substring);

        return repository.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase()
                        .contains(substring.trim().toLowerCase()))
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all users in the system.
     */
    public List<UserDto> list() {
        log.debug("Fetching the list of all users.");

        return repository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}
