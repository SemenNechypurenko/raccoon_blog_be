package i.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.dto.CommentDto;
import i.exception.CommentNotFoundException;
import i.interceptors.ControllerExceptionHandler;
import i.security.JwtUtils;
import i.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommentController.class)  // Automatically configures the controller for testing
@Import(ControllerExceptionHandler.class) // Import exception handlers
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "testUser") // Simulate user authentication
    @Test
    @Description("Test to create a comment and verify the response includes the correct values.")
    void createComment_ShouldReturnCreatedComment() throws Exception {
        // Arrange: create request DTO and expected response DTO
        final CommentCreateRequestDto requestDto = objectMapper.readValue(
                """
                        {
                            "itemId": "123",
                            "content": "This is a test comment",
                            "parentCommentId": null
                        }
                        """,
                CommentCreateRequestDto.class);

        final CommentCreateResponseDto responseDto = objectMapper.readValue(
                """
                        {
                            "id": "1",
                            "itemId": "123",
                            "content": "This is a test comment",
                            "username": "testUser",
                            "parentCommentId": null,
                            "createdAt": "2024-12-10T10:00:00Z"
                        }
                        """,
                CommentCreateResponseDto.class);

        // Mock the createComment service method
        Mockito.when(commentService.createComment(any(CommentCreateRequestDto.class), eq("testUser")))
                .thenReturn(responseDto);

        // Act & Assert: perform POST request and validate the response
        mockMvc.perform(post("/comments")
                        .with(csrf()) // Enable CSRF token for security
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.itemId").value("123"))
                .andExpect(jsonPath("$.content").value("This is a test comment"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.createdAt").value("2024-12-10T10:00:00Z"));
    }

    @Test
    @WithMockUser(username = "testUser") // Simulate user authentication
    @Description("Test to retrieve a comment by ID and verify the returned data matches the expected values.")
    void getCommentById_ShouldReturnComment() throws Exception {
        // Arrange: create CommentDto
        final CommentDto commentDto = objectMapper.readValue(
                """
                        {
                            "id": "1",
                            "itemId": "123",
                            "content": "This is a test comment",
                            "username": "testUser",
                            "parentCommentId": null,
                            "createdAt": "2024-12-10T10:00:00Z"
                        }
                        """,
                CommentDto.class);

        // Mock the getCommentById service method
        Mockito.when(commentService.getCommentById("1")).thenReturn(commentDto);

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/comments/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.itemId").value("123"))
                .andExpect(jsonPath("$.content").value("This is a test comment"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.createdAt").value("2024-12-10T10:00:00Z"));
    }

    @Test
    @WithMockUser(username = "testUser") // Simulate user authentication
    @Description("Test to retrieve all comments by post ID and verify the returned list contains the expected comments.")
    void getCommentsByPostId_ShouldReturnListOfComments() throws Exception {
        // Arrange: create a list of CommentDto objects
        final CommentDto commentDto1 = objectMapper.readValue(
                """
                        {
                            "id": "1",
                            "itemId": "123",
                            "content": "This is a test comment 1",
                            "username": "testUser",
                            "createdAt": "2024-12-10T10:00:00Z"
                        }
                        """,
                CommentDto.class);

        final CommentDto commentDto2 = objectMapper.readValue(
                """
                        {
                            "id": "2",
                            "itemId": "123",
                            "content": "This is a test comment 2",
                            "username": "testUser",
                            "createdAt": "2024-12-10T10:05:00Z"
                        }
                        """,
                CommentDto.class);

        // Mock the listCommentsByPostId service method
        Mockito.when(commentService.listCommentsByPostId("123"))
                .thenReturn(List.of(commentDto1, commentDto2));

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/comments/post/123")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Check that two comments are returned
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    @WithMockUser(username = "testUser") // Simulate user authentication
    @Description("Test to retrieve comments by post ID when no comments exist, expecting an empty list.")
    void getCommentsByPostId_ShouldReturnEmptyList() throws Exception {
        // Mock the listCommentsByPostId service method to return an empty list
        Mockito.when(commentService.listCommentsByPostId("999")).thenReturn(Collections.emptyList());

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/comments/post/999")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0)); // Check that the list is empty
    }

    @Test
    @WithMockUser(username = "testUser") // Simulate user authentication
    @Description("Test to handle the case where a comment does not exist, expecting a 404 Not Found response.")
    void getCommentById_ShouldReturnNotFoundWhenCommentDoesNotExist() throws Exception {
        // Mock the getCommentById service method to throw CommentNotFoundException
        Mockito.when(commentService.getCommentById("999")).thenThrow(new CommentNotFoundException("Comment not found"));

        // Act & Assert: perform GET request and expect 404 status
        mockMvc.perform(get("/comments/999")
                        .with(csrf()) // Enable CSRF token for security
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Description("Test to verify that creating a comment without authentication results in 401 Unauthorized.")
    void createComment_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        final CommentCreateRequestDto requestDto = objectMapper.readValue(
                """
                        {
                            "itemId": "123",
                            "content": "This is a test comment",
                            "parentCommentId": null
                        }
                        """,
                CommentCreateRequestDto.class);

        mockMvc.perform(post("/comments")
                        .with(csrf()) // Enable CSRF token for security
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to verify that fetching a comment by ID without authentication results in 401 Unauthorized.")
    void getCommentById_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/comments/1")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to verify that fetching comments by post ID without authentication results in 401 Unauthorized.")
    void getCommentsByPostId_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/comments/post/123")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to verify that trying to fetch a non-existing comment by ID without authentication results in 401 Unauthorized.")
    void getCommentById_ShouldReturnUnauthorizedWhenNotFoundAndNotAuthenticated() throws Exception {
        mockMvc.perform(get("/comments/999")
                        .with(csrf()) // Enable CSRF token for security
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to verify that an invalid request to create a comment without authentication results in 401 Unauthorized.")
    void createComment_ShouldReturnUnauthorizedForInvalidRequestWhenNotAuthenticated() throws Exception {
        final CommentCreateRequestDto requestDto = objectMapper.readValue(
                """
                        {
                            "itemId": "",
                            "content": "",
                            "parentCommentId": null
                        }
                        """,
                CommentCreateRequestDto.class);

        mockMvc.perform(post("/comments")
                        .with(csrf()) // Enable CSRF token for security
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @WithMockUser(username = "testUser") // Simulate user authentication
    @Description("Test to retrieve a list of comments by username and verify the returned list contains the expected comments.")
    void getListOfComments_ShouldReturnListOfCommentsByUser() throws Exception {
        // Arrange: create a list of CommentDto objects
        final CommentDto commentDto1 = objectMapper.readValue(
                """
                        {
                            "id": "1",
                            "itemId": "123",
                            "content": "This is a test comment 1",
                            "username": "testUser",
                            "createdAt": "2024-12-10T10:00:00Z"
                        }
                        """,
                CommentDto.class);

        final CommentDto commentDto2 = objectMapper.readValue(
                """
                        {
                            "id": "2",
                            "itemId": "123",
                            "content": "This is a test comment 2",
                            "username": "testUser",
                            "createdAt": "2024-12-10T10:05:00Z"
                        }
                        """,
                CommentDto.class);

        // Mock the getCommentForUserByUserId service method
        Mockito.when(commentService.getCommentForUserByUserId("testUser"))
                .thenReturn(List.of(commentDto1, commentDto2));

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/comments/user/testUser")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Check that two comments are returned
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[0].content").value("This is a test comment 1"))
                .andExpect(jsonPath("$[1].content").value("This is a test comment 2"))
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[1].username").value("testUser"))
                .andExpect(jsonPath("$[0].createdAt").value("2024-12-10T10:00:00Z"))
                .andExpect(jsonPath("$[1].createdAt").value("2024-12-10T10:05:00Z"));
    }

    @Test
    @WithMockUser(username = "testUser") // Simulate user authentication
    @Description("Test to retrieve a list of comments by username when no comments exist, expecting an empty list.")
    void getListOfComments_ShouldReturnEmptyListWhenNoCommentsExist() throws Exception {
        // Mock the getCommentForUserByUserId service method to return an empty list
        Mockito.when(commentService.getCommentForUserByUserId("testUser"))
                .thenReturn(Collections.emptyList());

        // Act & Assert: perform GET request and validate the response
        mockMvc.perform(get("/comments/user/testUser")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0)); // Check that the list is empty
    }

}
