package i.controller;

import i.controller.AuthController;
import i.repository.UserRepository;
import i.security.JwtAuthFilter;
import i.security.JwtUtils;
import i.security.SecurityConfig;
import i.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class) // Импортируем вашу конфигурацию безопасности
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

    @Test
    public void shouldReturn403ForInvalidToken() throws Exception {
        // Мокируем поведение JwtUtils
        Mockito.when(jwtUtils.extractUsername("invalid_token")).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/auth/check")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().is(401));
    }

    @Test
    public void shouldReturn200ForValidToken() throws Exception {
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testuser");

        // Мокируем возвращение пользователя
        Mockito.when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);

        // Мокируем токен
        Mockito.when(jwtUtils.extractUsername("valid_token")).thenReturn("testuser");
        Mockito.when(jwtUtils.isTokenValid(Mockito.eq("valid_token"), Mockito.eq(mockUserDetails)))
                .thenReturn(true);

        // Выполняем запрос
        mockMvc.perform(get("/auth/check")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk());
    }

}
