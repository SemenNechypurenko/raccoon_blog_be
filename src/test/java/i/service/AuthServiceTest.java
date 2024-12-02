package i.service;

import i.dto.AuthenticationRequestDto;
import i.dto.TokenDto;
import i.dto.UserCreateResponseDto;
import i.exception.EmailNotVerifiedException;
import i.model.User;
import i.repository.UserRepository;
import i.security.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should return valid token when authentication successful")
    void shouldReturnValidToken() {
        // Arrange: Set up the test data and mocks
        String username = "testUser";
        String password = "testPass";
        String generatedToken = "mockJwtToken";

        // Create an AuthenticationRequestDto to simulate the input from the client
        AuthenticationRequestDto authRequestDto = new AuthenticationRequestDto(username, password);

        // Create a mock UserDetails object representing a successful authentication
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .roles("USER")
                .build();

        // Create a mock User object returned by the UserRepository
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setEmailVerified(true);
;
        // Create the expected UserCreateRequestDto object to map from User
        UserCreateResponseDto userCreateResponseDto = new UserCreateResponseDto();

        // Mock the repository call to return the mock User when looking up by username
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Mock the UserDetailsService to return the mock UserDetails when the username is loaded
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Mock the JwtUtils to return a mock token when generateToken is called
        when(jwtUtils.generateToken(userDetails)).thenReturn(generatedToken);

        // Mock the ModelMapper to map the User object to UserCreateRequestDto
        when(modelMapper.map(mockUser, UserCreateResponseDto.class)).thenReturn(userCreateResponseDto);

        // Act: Call the service method to get the token
        TokenDto result = authService.token(authRequestDto);

        // Assert: Check that the returned token and user information are as expected
        assertEquals(generatedToken, result.getToken());  // Ensure the token matches the mocked token
        assertEquals(userCreateResponseDto, result.getUser());  // Ensure the UserCreateRequestDto matches the mock

        // Verify that the mock methods were called exactly once with the correct arguments
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)  // Check that the authentication was attempted with the correct credentials
        );
        verify(userDetailsService, times(1)).loadUserByUsername(username);  // Ensure the user details were loaded
        verify(jwtUtils, times(1)).generateToken(userDetails);  // Ensure a token was generated for the user
        verify(userRepository, times(1)).findByUsername(username);  // Ensure the user repository was queried for the username
        verify(modelMapper, times(1)).map(mockUser, UserCreateResponseDto.class);  // Ensure the User object was mapped correctly
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication fails")
    void shouldThrowBadCredentialsExceptionWhenAuthenticationFails() {
        // Arrange: Set up test data
        String username = "testUser";
        String password = "wrongPass";
        AuthenticationRequestDto authRequestDto = new AuthenticationRequestDto(username, password);

        // Mock the AuthenticationManager to throw BadCredentialsException
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Act & Assert: Ensure the token method throws the correct exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.token(authRequestDto));

        // Check the exception message
        assertEquals("Bad credentials", exception.getMessage());

        // Verify that no other methods are called after authentication failure
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtUtils, never()).generateToken(any());
        verify(modelMapper, never()).map(any(), eq(UserCreateResponseDto.class));
    }

    @Test
    @DisplayName("Should throw EmailNotVerifiedException when email is not verified")
    void shouldThrowEmailNotVerifiedExceptionWhenEmailNotVerified() {
        // Arrange: Set up test data
        String username = "testUser";
        String password = "testPass";

        AuthenticationRequestDto authRequestDto = new AuthenticationRequestDto(username, password);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .roles("USER")
                .build();

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setEmailVerified(false);  // Email is not verified

        // Mock authentication to pass
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenReturn(null);

        // Mock UserDetailsService and UserRepository behavior
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act & Assert: Ensure EmailNotVerifiedException is thrown
        EmailNotVerifiedException exception = assertThrows(
                EmailNotVerifiedException.class,
                () -> authService.token(authRequestDto)
        );

        // Check the exception message
        assertEquals("Email not verified", exception.getMessage());

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(userRepository, times(1)).findByUsername(username);

        // Ensure no interactions with JwtUtils and ModelMapper since exception was thrown
        verify(jwtUtils, never()).generateToken(any());
        verify(modelMapper, never()).map(any(), eq(UserCreateResponseDto.class));
    }

}
