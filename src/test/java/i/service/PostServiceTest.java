package i.service;

import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.dto.PostFileUrlDto;
import i.fileStorageClient.FileStorage;
import i.model.Post;
import i.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Description;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class PostServiceTest {

    @Mock
    private PostRepository repository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private PostDto testPostDto;
    private PostCreateResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testPost = new Post();
        testPost.setId("1");
        testPost.setTitle("Test Title");
        testPost.setContent("Test Content");
        testPost.setUsername("user1");
        testPost.setCreatedAt(LocalDateTime.now());

        testPostDto = new PostDto();
        testPostDto.setId("1");
        testPostDto.setTitle("Test Title");
        testPostDto.setContent("Test Content");
        testPostDto.setUsername("user1");

        testResponseDto = new PostCreateResponseDto();
        testResponseDto.setId("1");
        testResponseDto.setTitle("Test Title");
    }

    @Test
    @Description("Should return a PostCreateResponseDto when valid data is provided.")
    void createPost_ShouldReturnPostCreateResponseDto_WhenValidData() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileStorage.uploadImage(mockFile)).thenReturn(Mono.just("http://image.url"));
        when(repository.save(any(Post.class))).thenReturn(testPost);
        when(mapper.map(testPost, PostCreateResponseDto.class)).thenReturn(testResponseDto);

        // Act
        PostCreateResponseDto result = postService.createPost("Test Title", "Test Content", mockFile, "user1");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test Title", result.getTitle());
        verify(repository, times(1)).save(any(Post.class));
    }

    @Test
    @Description("Should return a list of posts when list() is called without filters.")
    void list_ShouldReturnListOfPosts_WhenCalled() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(testPost));
        when(mapper.map(testPost, PostDto.class)).thenReturn(testPostDto);

        // Act
        List<PostDto> result = postService.list(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        verify(repository, times(1)).findAll();
    }

    @Test
    @Description("Should return a PostDto when the post exists by ID.")
    void getPostById_ShouldReturnPostDto_WhenPostExists() {
        // Arrange
        when(repository.findById("1")).thenReturn(Optional.of(testPost));
        when(mapper.map(testPost, PostDto.class)).thenReturn(testPostDto);

        // Act
        PostDto result = postService.getPostById("1");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test Title", result.getTitle());
        verify(repository, times(1)).findById("1");
    }

    @Test
    @Description("Should throw an exception when the post is not found by ID.")
    void getPostById_ShouldThrowException_WhenPostNotFound() {
        // Arrange
        when(repository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.getPostById("1"));
        assertEquals("Post not found", exception.getMessage());
        verify(repository, times(1)).findById("1");
    }

    @Test
    @Description("Should return PostFileUrlDto when the post has an associated image URL.")
    void getImageUrlByPostId_ShouldReturnPostFileUrlDto_WhenImageUrlExists() {
        // Arrange
        testPost.setImageUrl("http://image.url");
        when(repository.findById("1")).thenReturn(Optional.of(testPost));

        // Act
        PostFileUrlDto result = postService.getImageUrlByPostId("1");

        // Assert
        assertNotNull(result);
        assertEquals("http://image.url", result.getPostFileUrl());
        verify(repository, times(1)).findById("1");
    }

    @Test
    @Description("Should throw an exception when the post does not have an image URL.")
    void getImageUrlByPostId_ShouldThrowException_WhenImageUrlDoesNotExist() {
        // Arrange
        when(repository.findById("1")).thenReturn(Optional.of(testPost));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.getImageUrlByPostId("1"));
        assertEquals("Post with ID 1 does not have an associated image.", exception.getMessage());
    }

    @Test
    @Description("Should throw an exception when the post is not found by ID for image URL retrieval.")
    void getImageUrlByPostId_ShouldThrowException_WhenPostNotFound() {
        // Arrange
        when(repository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.getImageUrlByPostId("1"));
        assertEquals("Post not found with ID: 1", exception.getMessage());
    }
}