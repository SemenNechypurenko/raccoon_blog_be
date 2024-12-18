package i.service;

import i.dto.UserCreateRequestDto;
import i.dto.UserDto;
import i.model.Role;
import i.model.User;
import i.repository.RoleRepository;
import i.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private UserCreateRequestDto userCreateRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        userCreateRequestDto = new UserCreateRequestDto();
        userCreateRequestDto.setUsername("testuser");
        userCreateRequestDto.setEmail("test@example.com");

        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setEmailVerified(false);

        userService = new UserService(userRepository, roleRepository, modelMapper, emailService);
    }

    @Test
    @DisplayName("Should successfully create a new user")
    public void save_success() {
        // Create test data
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto("testuser", "test@example.com", null, new HashSet<>());

        // Mock the return of the role when searching by name
        Mockito.when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role("ROLE_USER")));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // Create an instance of UserService with mocks
        UserService userService = new UserService(userRepository, roleRepository, modelMapper, emailService);

        // Perform the test action
        UserDto result = userService.save(userCreateRequestDto);

        // Verify results
        Assertions.assertNotNull(result);
        Assertions.assertEquals("testuser", result.getUsername());
        Assertions.assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username already exists")
    void save_usernameAlreadyExists() {
        // Mock behavior for an existing username
        Mockito.when(userRepository.findByUsername(userCreateRequestDto.getUsername())).thenReturn(Optional.of(user));

        // Assert that the exception is thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.save(userCreateRequestDto));

        // Verify interactions with the repository
        Mockito.verify(userRepository).findByUsername("testuser");
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email already exists")
    void save_emailAlreadyExists() {
        // Mock behavior for an existing email
        Mockito.when(userRepository.findByUsername(userCreateRequestDto.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(userCreateRequestDto.getEmail())).thenReturn(Optional.of(user));

        // Assert that the exception is thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.save(userCreateRequestDto));

        // Verify interactions with the repository
        Mockito.verify(userRepository).findByEmail("test@example.com");
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Should confirm email successfully when token is valid")
    void confirmEmail_success() {
        String confirmationToken = "valid-token";
        user.setConfirmationToken(confirmationToken);

        // Mock repository to return the user by confirmation token
        Mockito.when(userRepository.findByConfirmationToken(confirmationToken)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // Call the service method
        userService.confirmEmail(confirmationToken);

        // Verify that the user's email is confirmed
        Assertions.assertTrue(user.isEmailVerified(), "The user's email should be confirmed");

        // Verify interactions with the repository
        Mockito.verify(userRepository).findByConfirmationToken(confirmationToken);
        Mockito.verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw RuntimeException when confirmation token is invalid")
    void confirmEmail_invalidToken() {
        String invalidToken = "invalid-token";

        // Mock repository to return empty when searching for the invalid token
        Mockito.when(userRepository.findByConfirmationToken(invalidToken)).thenReturn(Optional.empty());

        // Assert that the exception is thrown
        Assertions.assertThrows(RuntimeException.class, () -> userService.confirmEmail(invalidToken));

        // Verify interactions with the repository
        Mockito.verify(userRepository).findByConfirmationToken(invalidToken);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }
}