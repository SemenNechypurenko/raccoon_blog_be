package i.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import i.dto.AuthenticationRequestDto;
import i.dto.TokenDto;
import i.dto.UserDto;
import i.exception.EmailNotVerifiedException;
import i.interceptors.ControllerExceptionHandler;
import i.service.AuthService;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Set up the MockMvc instance with the controller and the exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    @Description("Should return valid token when correct credentials")
    void shouldReturnToken_whenLoginRequestIsValid() throws Exception {
        // Mock request and response DTOs
        final AuthenticationRequestDto authRequestDto = objectMapper.readValue(
                """
                        {
                               "username": "testUser",
                               "password": "testPass"
                           }
                        """,
                AuthenticationRequestDto.class);

        final UserDto userDto = objectMapper.readValue(
                """
                        {
                            "id": "6747b35f4d3f9466bc949b76",
                            "username": "testUser",
                            "email": "testUser@mail.de",
                            "confirmationToken": "test-confirmation-token",
                            "roles": []
                        }
                        """,
                UserDto.class);

        final TokenDto mockTokenDto = new TokenDto(userDto, "mockJwtToken");

        // Mock the behavior of the AuthService
        when(authService.token(authRequestDto)).thenReturn(mockTokenDto);

        // Execute the request and verify the response
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                               "username": "testUser",
                                               "password": "testPass"
                                           }
                                        """
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockJwtToken"))
                .andExpect(jsonPath("$.user.username").value("testUser"));
    }

    @Test
    @Description("Should return 403 status when email is not verified")
    void shouldReturn403Response_whenEmailNotVerified() throws Exception {
        // Mock request DTO
        final AuthenticationRequestDto authRequestDto = objectMapper.readValue(
                """
                        {
                               "username": "unverifiedUser",
                               "password": "testPass"
                           }
                        """,
                AuthenticationRequestDto.class);

        // Mock behavior of AuthService to throw EmailNotVerifiedException
        when(authService.token(authRequestDto)).thenThrow(new EmailNotVerifiedException("Email is not verified"));

        // Execute the request and verify the response
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                               "username": "unverifiedUser",
                                               "password": "testPass"
                                           }
                                        """
                        ))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Email is not verified"));
    }


    @Test
    @Description("Should return bad request status when username is not entered")
    void shouldReturn400Response_whenUsernameIsEmpty() throws Exception {
        // Simulate a request with an empty username
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                               "username": "",
                                               "password": "testPass"
                                           }
                                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username should not be empty or blank"));
    }

    @Test
    @Description("Should return bad request status when password is not entered")
    void shouldReturn400Response_whenPasswordIsEmpty() throws Exception {
        // Simulate a request with an empty password
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                               "username": "testUser",
                                               "password": ""
                                           }
                                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password should not be empty or blank"));
    }

    @Test
    @Description("Should return bad request status when one field is empty and another is null")
    void shouldReturn400Response_whenOneFieldEmptyAndAnotherNull() throws Exception {
        // Simulate a request with an empty username and null password
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                               "username": "",
                                               "password": null
                                           }
                                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username should not be empty or blank"))
                .andExpect(jsonPath("$.password").value("Password should not be empty or blank"));
    }
}
