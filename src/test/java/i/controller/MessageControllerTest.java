package i.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import i.dto.MessageDto;
import i.security.JwtUtils;
import i.service.MessageService;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtils jwtUtils;

    @WithMockUser(username = "testUser") // Simulate user authentication
    @Test
    @Description("Test to send a message and verify the returned MessageDto contains the correct data.")
    void sendMessage_ShouldReturnMessageDto() throws Exception {
        final MessageDto responseDto = new MessageDto(
                "1", "testUser", "recipientUser", "Hello, world!", LocalDateTime.now(), new HashSet<>());

        // Mock the service method
        Mockito.when(messageService.sendMessage(eq("recipientUser"), eq("testUser"), eq("Hello, world!")))
                .thenReturn(responseDto);

        // Act & Assert: perform POST request and pass 'recipient' and 'content' as query parameters
        mockMvc.perform(post("/messages")
                        .with(csrf()) // Enable CSRF token for security
                        .param("recipient", "recipientUser")
                        .param("content", "Hello, world!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.sender").value("testUser"))
                .andExpect(jsonPath("$.recipient").value("recipientUser"))
                .andExpect(jsonPath("$.content").value("Hello, world!"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty()) // Validate the timestamp
                .andExpect(jsonPath("$.commentIds").isEmpty()); // Validate that commentIds is empty
    }

    @WithMockUser(username = "testUser") // Simulate user authentication
    @Test
    @Description("Test to retrieve a list of messages sent by the authenticated user.")
    void getSentMessages_ShouldReturnListOfMessages() throws Exception {
        // Arrange: create a list of MessageDto objects
        final MessageDto messageDto1 = new MessageDto("1", "testUser", "recipient1", "Message 1", LocalDateTime.now(), new HashSet<>());
        final MessageDto messageDto2 = new MessageDto("2", "testUser", "recipient2", "Message 2", LocalDateTime.now(), new HashSet<>());

        // Mock the service method
        Mockito.when(messageService.getMessagesBySender("testUser"))
                .thenReturn(List.of(messageDto1, messageDto2));

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/messages/sent")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Expect two messages
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].sender").value("testUser"))
                .andExpect(jsonPath("$[0].recipient").value("recipient1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].sender").value("testUser"))
                .andExpect(jsonPath("$[1].recipient").value("recipient2"));
    }

    @WithMockUser(username = "testUser") // Simulate user authentication
    @Test
    @Description("Test to retrieve a list of messages received by the authenticated user.")
    void getReceivedMessages_ShouldReturnListOfMessages() throws Exception {
        // Arrange: create a list of MessageDto objects
        final MessageDto messageDto1 = new MessageDto("1", "sender1", "testUser", "Received Message 1", LocalDateTime.now(), new HashSet<>());
        final MessageDto messageDto2 = new MessageDto("2", "sender2", "testUser", "Received Message 2", LocalDateTime.now(), new HashSet<>());

        // Mock the service method
        Mockito.when(messageService.getMessagesByRecipient("testUser"))
                .thenReturn(List.of(messageDto1, messageDto2));

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/messages/received")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Expect two messages
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].recipient").value("testUser"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].recipient").value("testUser"));
    }

    @WithMockUser(username = "testUser") // Simulate user authentication
    @Test
    @Description("Test to retrieve a single message by its ID.")
    void getMessageById_ShouldReturnMessageDto() throws Exception {
        // Arrange: create MessageDto
        final MessageDto messageDto = new MessageDto("1", "sender1", "testUser", "Test Message", LocalDateTime.now(), new HashSet<>());

        // Mock the service method
        Mockito.when(messageService.getMessagesById("1", "testUser"))
                .thenReturn(messageDto);

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/messages/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.sender").value("sender1"))
                .andExpect(jsonPath("$.recipient").value("testUser"))
                .andExpect(jsonPath("$.content").value("Test Message"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty()) // Validate the timestamp
                .andExpect(jsonPath("$.commentIds").isEmpty()); // Validate that commentIds is empty
    }

    @Test
    @Description("Test to ensure unauthorized access when sending a message without authentication.")
    void sendMessage_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Arrange: create request DTO
        final MessageDto requestDto = new MessageDto(
                null, "testUser", "recipientUser", "Hello, world!", null, new HashSet<>());

        // Act & Assert: perform POST request without authentication and expect 401 Unauthorized
        mockMvc.perform(post("/messages")
                        .with(csrf()) // Enable CSRF token for security
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve a message by ID without authentication.")
    void getMessageById_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/messages/1")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve sent messages without authentication.")
    void getSentMessages_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/messages/sent")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve received messages without authentication.")
    void getReceivedMessages_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/messages/received")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }
}
