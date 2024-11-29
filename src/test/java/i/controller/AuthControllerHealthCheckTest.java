package i.controller;

import i.repository.UserRepository;
import i.security.JwtUtils;
import i.security.SecurityConfig;
import i.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for AuthController to check health and authorization with various token cases.
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class) // Import security configuration
public class AuthControllerHealthCheckTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    /**
     * Test case for invalid token in the Authorization header.
     * Expected status: HTTP 401 (Unauthorized).
     */
    @Test
    @DisplayName("Should return 401 for invalid token")
    public void shouldReturn401ForInvalidToken() throws Exception {
        // Mock behavior for JwtUtils when token is invalid
        Mockito.when(jwtUtils.extractUsername("invalid_token"))
                .thenThrow(new RuntimeException("Invalid token"));

        // Perform GET request with an invalid token
        mockMvc.perform(get("/auth/check")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().is(401));
    }

    /**
     * Test case for valid token in the Authorization header.
     * Expected status: HTTP 200 (OK).
     */
    @Test
    @DisplayName("Should return 200 for valid token")
    public void shouldReturn200ForValidToken() throws Exception {
        // Mock UserDetails and behavior for valid token
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testuser");

        // Mock loading user details and token validation
        Mockito.when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(mockUserDetails);
        Mockito.when(jwtUtils.extractUsername("valid_token")).thenReturn("testuser");
        Mockito.when(jwtUtils.isTokenValid(Mockito.eq("valid_token"), Mockito.eq(mockUserDetails)))
                .thenReturn(true);

        // Perform GET request with a valid token
        mockMvc.perform(get("/auth/check")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk());
    }

    /**
     * Test case for missing token in the Authorization header.
     * Expected status: HTTP 403 (Forbidden).
     */
    @Test
    @DisplayName("Should return 403 when token is missing")
    public void shouldReturn403WhenTokenIsMissing() throws Exception {
        // Perform GET request without any token
        mockMvc.perform(get("/auth/check"))
                .andExpect(status().isForbidden());
    }
}
