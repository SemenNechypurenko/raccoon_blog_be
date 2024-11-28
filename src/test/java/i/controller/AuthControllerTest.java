package i.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import i.dto.AuthenticationRequestDto;
import i.dto.TokenDto;
import i.dto.UserCreateResponseDto;
import i.service.AuthService;
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
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void shouldReturnToken_whenLoginRequestIsValid() throws Exception {
        final AuthenticationRequestDto authRequestDto = objectMapper.readValue(
                """
                        {
                               "username": "testUser",
                               "password": "testPass"
                           }
                        """,
                AuthenticationRequestDto.class);

        final UserCreateResponseDto userCreateResponseDto = objectMapper.readValue(
                """
                        {
                            "id": "6747b35f4d3f9466bc949b76",
                            "username": "testUser",
                            "email": "testUser@mail.de",
                            "roles": []
                        }
                        """,
                UserCreateResponseDto.class);

        final TokenDto mockTokenDto = new TokenDto(userCreateResponseDto, "mockJwtToken");

        // Mock the behavior of the AuthService
        when(authService.token(authRequestDto)).thenReturn(mockTokenDto);

        // Execute and Assert
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


}

