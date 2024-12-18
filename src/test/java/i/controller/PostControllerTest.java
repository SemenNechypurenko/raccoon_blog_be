package i.controller;

import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.dto.PostFileUrlDto;
import i.security.JwtUtils;
import i.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtUtils jwtUtils;

    @WithMockUser(username = "testUser")
    @Test
    @Description("Test to create a post and verify the returned PostCreateResponseDto.")
    void createPost_ShouldReturnPostCreateResponseDto() throws Exception {
        // Arrange: mock response
        PostCreateResponseDto responseDto = new PostCreateResponseDto();
        responseDto.setId("1");
        responseDto.setTitle("Sample Title");
        responseDto.setContent("Sample Content");
        responseDto.setUsername("testUser");
        responseDto.setCreatedAt("2023-12-18T12:34:56");
        responseDto.setImageUrl("https://example.com/image.jpg");

        // Mock service
        Mockito.when(postService.createPost(eq("Sample Title"), eq("Sample Content"), eq(null), eq("testUser")))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(multipart("/posts")
                        .file(new MockMultipartFile("title", "Sample Title".getBytes())) // Passing title as a form part
                        .file(new MockMultipartFile("content", "Sample Content".getBytes())) // Passing content as a form part
                        .with(csrf())
                        .contentType("multipart/form-data")) // Explicit content type for multipart
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Sample Title"))
                .andExpect(jsonPath("$.content").value("Sample Content"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.createdAt").value("2023-12-18T12:34:56"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"));
    }

    @WithMockUser(username = "testUser")
    @Test
    @Description("Test to retrieve a list of posts.")
    void getListOfPosts_ShouldReturnListOfPosts() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId("1");
        postDto.setTitle("Sample Title");
        postDto.setContent("Sample Content");
        postDto.setUsername("author");
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setTags(new HashSet<>(List.of("tag1", "tag2")));
        postDto.setCommentIds(new HashSet<>(List.of("comment1", "comment2")));
        postDto.setImageUrl("https://example.com/image.jpg");

        Mockito.when(postService.list(null)).thenReturn(List.of(postDto));

        // Execute the request and verify the response
        mockMvc.perform(get("/posts")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Sample Title"))
                .andExpect(jsonPath("$[0].username").value("author"))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/image.jpg"));
    }

    @WithMockUser(username = "testUser")
    @Test
    @Description("Test to retrieve a single post by its ID.")
    void getById_ShouldReturnPostDto() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId("1");
        postDto.setTitle("Sample Title");
        postDto.setContent("Sample Content");
        postDto.setUsername("author");
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setTags(new HashSet<>(List.of("tag1", "tag2")));
        postDto.setCommentIds(new HashSet<>(List.of("comment1", "comment2")));
        postDto.setImageUrl("https://example.com/image.jpg");

        Mockito.when(postService.getPostById("1")).thenReturn(postDto);

        // Execute the request and verify the response
        mockMvc.perform(get("/posts/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Sample Title"))
                .andExpect(jsonPath("$.username").value("author"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$.tags.size()").value(2));
    }

    @WithMockUser(username = "testUser")
    @Test
    @Description("Test to retrieve image URL of a post.")
    void getImageUrl_ShouldReturnPostFileUrlDto() throws Exception {
        PostFileUrlDto postFileUrlDto = new PostFileUrlDto("https://example.com/image.jpg");

        Mockito.when(postService.getImageUrlByPostId("1")).thenReturn(postFileUrlDto);

        // Execute the request and verify the response
        mockMvc.perform(get("/posts/1/image-url")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postFileUrl").value("https://example.com/image.jpg"));
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to create a post without authentication.")
    void createPost_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform POST request without authentication and expect 401 Unauthorized
        mockMvc.perform(multipart("/posts")
                        .file(new MockMultipartFile("title", "Sample Title".getBytes()))
                        .file(new MockMultipartFile("content", "Sample Content".getBytes()))
                        .with(csrf())
                        .contentType("multipart/form-data"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve a list of posts without authentication.")
    void getListOfPosts_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/posts")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve a post by ID without authentication.")
    void getById_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/posts/1")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }

    @Test
    @Description("Test to ensure unauthorized access when attempting to retrieve image URL of a post without authentication.")
    void getImageUrl_ShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Act & Assert: perform GET request without authentication and expect 401 Unauthorized
        mockMvc.perform(get("/posts/1/image-url")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }
}
