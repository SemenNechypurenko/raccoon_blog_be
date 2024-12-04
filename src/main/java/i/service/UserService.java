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
import org.springframework.util.StringUtils;


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

    public UserDto save(UserCreateRequestDto userCreateRequestDto) {
        // Checking the uniqueness of the username and email
        validateUniqueUser(userCreateRequestDto.getUsername(), userCreateRequestDto.getEmail());
        User user = convertToEntity(userCreateRequestDto);

        final String confirmationToken = UUID.randomUUID().toString();
        user.setConfirmationToken(confirmationToken);

        user = repository.save(user);

        // Отправляем email с ссылкой для подтверждения
        emailService.sendConfirmationEmail(user.getEmail(), confirmationToken);

        return convertFromEntity(user);
    }

    /**
     * Convert the UserCreateRequestDto entity to User.
     */
    public User convertToEntity(UserCreateRequestDto userCreateRequestDto) {
        // Use ModelMapper for basic mapping
        User user = modelMapper.map(userCreateRequestDto, User.class);
        // Set and validate roles by getting them from RoleRepository
        Set<Role> roles = getValidatedRoles(userCreateRequestDto.getRoles());
        user.setRoles(roles);
        return user;
    }

    /**
     * Check if the username and email are unique, and throw an exception if they already exist.
     */
    private void validateUniqueUser(String username, String email) {
        Optional<User> userByUsername = repository.findByUsername(username);
        if (userByUsername.isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        Optional<User> userByEmail = repository.findByEmail(email);
        if (userByEmail.isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    /**
     * Convert the User entity to UserDto.
     */
    public UserDto convertFromEntity(User user) {
        // Use ModelMapper for basic mapping
        UserDto responseDto = modelMapper.map(user, UserDto.class);

        // Set the roles for the response
        Set<RoleDto> roleDtos = user.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        responseDto.setRoles(roleDtos);

        return responseDto;
    }

    /**
     * Check the existence of roles from the DTO in the database, returns Set<Role>.
     */
    private Set<Role> getValidatedRoles(Set<RoleDto> roleDtos) {
        Set<Role> roles = new HashSet<>();

        // If roles are not specified, add the default role "ROLE_USER"
        if (roleDtos.isEmpty()) {
            roles.add(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role 'ROLE_USER' not found")));
        } else {
            // Check the existence of each role in the database
            for (RoleDto roleDto : roleDtos) {
                Role role = roleRepository.findByName(roleDto.getName())
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleDto.getName()));
                roles.add(role);
            }
        }
        return roles;
    }

    /**
     * Confirms the user's email by verifying the token.
     * This method is now part of the service layer.
     */
    public void confirmEmail(String token) {
        User user = repository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Confirmation token not found"));
        // Confirm the user's email and clear the token
        user.setEmailVerified(true);
        user.setConfirmationToken(null);  // Token is no longer needed after confirmation
        repository.save(user);  // Save the updated user
    }

    public List<UserDto> getUsernamesListBySubstring(String substring) {

        List<User> allUsers = repository.findAll();

        if (!StringUtils.hasText(substring)) {
            return allUsers.stream()
                    .map(user -> modelMapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
        }

        // Фильтруем по подстроке, игнорируя регистр
        return allUsers.stream()
                .filter(user -> user.getUsername().toLowerCase()
                        .contains(substring.trim().toLowerCase()))
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}
