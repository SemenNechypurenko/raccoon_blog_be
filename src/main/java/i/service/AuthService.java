package i.service;

import i.dto.AuthenticationRequestDto;
import i.dto.TokenDto;
import i.dto.UserDto;
import i.exception.EmailNotVerifiedException;
import i.model.User;
import i.repository.UserRepository;
import i.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Authenticates the user and generates a JWT token.
     * @param dto The authentication request DTO containing the username and password.
     * @return The generated token DTO containing the user details and the JWT token.
     */
    public TokenDto token(AuthenticationRequestDto dto) {
        log.debug("Attempting authentication for user: {}", dto.getUsername());

        // Authenticate the user by username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        // Load user details from UserDetailsService
        final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsername());

        // Retrieve the current user from the database
        final User currentUser = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", dto.getUsername());
                    return new RuntimeException("User not found");
                });

        // Check if the user's email is verified
        if (!currentUser.isEmailVerified()) {
            log.warn("User's email not verified: {}", dto.getUsername());
            throw new EmailNotVerifiedException("Email not verified");
        }

        // Map the current user to UserDto for response
        UserDto userDto = modelMapper.map(currentUser, UserDto.class);
        log.debug("User authenticated successfully: {}", dto.getUsername());

        // Generate the JWT token
        String token = jwtUtils.generateToken(userDetails);
        log.debug("JWT token generated for user: {}", dto.getUsername());

        // Return the token DTO containing user details and token
        return new TokenDto(userDto, token);
    }
}
