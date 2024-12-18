package i.controller;

import i.dto.UserCreateRequestDto;
import i.dto.UserDto;
import i.security.JwtUtils;
import i.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(username = "testUser")
    @Description("Test to register a new user and verify the returned UserDto.")
    void registerUser_ShouldReturnUserDto() throws Exception {
        // Arrange: mock response
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto();
        userCreateRequestDto.setUsername("testUser");
        userCreateRequestDto.setPassword("testPassword");
        userCreateRequestDto.setEmail("tesе@test.com");


        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");

        // Mock service
        Mockito.when(userService.save(any(UserCreateRequestDto.class))).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\", \"password\":\"testPassword\", \"email\":\"tesе@test.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"));
    }


    @WithMockUser(username = "testUser")
    @Test
    @Description("Test to retrieve a list of users.")
    void getListOfUsers_ShouldReturnListOfUsers() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");

        Mockito.when(userService.list()).thenReturn(List.of(userDto));

        // Execute the request and verify the response
        mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @WithMockUser(username = "testUser")
    @Test
    @Description("Test to retrieve a list of users by substring.")
    void getUsersBySubstring_ShouldReturnListOfUsers() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");

        Mockito.when(userService.getUsernamesListBySubstring("test")).thenReturn(List.of(userDto));

        // Execute the request and verify the response
        mockMvc.perform(get("/users/test")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to register without authentication.")
    void registerUser_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform POST request without authentication and expect 401 Unauthorized
        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\", \"password\":\"testPassword\"}"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve a list of users without authentication.")
    void getListOfUsers_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve users by substring without authentication.")
    void getUsersBySubstring_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/users/test")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @WithMockUser(username = "testUser")
    @Description("Test to confirm user email based on token.")
    void confirmEmail_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(userService).confirmEmail(eq("test-token"));

        // Act & Assert
        mockMvc.perform(get("/users/confirm-email")
                        .with(csrf())
                        .param("token", "test-token")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }
}
