package i.service;

import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.dto.CommentDto;
import i.exception.CommentNotFoundException;
import i.exception.MessageOrPostNotFoundException;
import i.model.Comment;
import i.model.Post;
import i.repository.CommentRepository;
import i.repository.MessageRepository;
import i.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentCreateRequestDto requestDto;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        requestDto = new CommentCreateRequestDto();
        requestDto.setItemId("post1");
        requestDto.setContent("Test comment");

        comment = new Comment();
        comment.setId("comment1");
        comment.setItemId("post1");
        comment.setContent("Test comment");
        comment.setUsername("user1");
        comment.setParentCommentId(null);
        comment.setCreatedAt(LocalDateTime.now());

        post = new Post();
        post.setId("post1");
    }

    @Test
    @DisplayName("Should successfully create a comment when post exists")
    void createComment_success() {
        // Mock repository responses
        when(postRepository.findById("post1")).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            savedComment.setId("comment1");
            return savedComment;
        });

        // Mock ModelMapper mapping
        CommentCreateResponseDto mockedResponse = new CommentCreateResponseDto(
                "comment1", "post1", "Test comment", "user1", null, comment.getCreatedAt().toString());
        when(modelMapper.map(any(Comment.class), eq(CommentCreateResponseDto.class))).thenReturn(mockedResponse);

        // Call the service method
        CommentCreateResponseDto response = commentService.createComment(requestDto, "user1");

        // Verify the response
        assertNotNull(response, "The response should not be null");
        assertEquals("comment1", response.getId(), "The comment ID should be 'comment1'");
        assertEquals("post1", response.getItemId(), "The item ID should be 'post1'");
        assertEquals("Test comment", response.getContent(), "The content should be 'Test comment'");
        assertEquals("user1", response.getUsername(), "The username should be 'user1'");
        assertNull(response.getParentCommentId(), "The parent comment ID should be null");

        // Verify interactions with repositories
        verify(postRepository).findById("post1");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw MessageOrPostNotFoundException when post and message are not found")
    void createComment_itemNotFound_throwsException() {
        when(postRepository.findById("post1")).thenReturn(Optional.empty());
        when(messageRepository.findById("post1")).thenReturn(Optional.empty());

        // Assert that the exception is thrown
        assertThrows(MessageOrPostNotFoundException.class,
                () -> commentService.createComment(requestDto, "user1"));

        // Ensure that the comment is not saved when the post is not found
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully retrieve a comment by ID")
    void getCommentById_success() {
        when(commentRepository.findById("comment1")).thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(new CommentDto("comment1", "post1", "Test comment", "user1", null, comment.getCreatedAt().toString()));

        // Call the service method
        CommentDto result = commentService.getCommentById("comment1");

        // Verify the result
        assertNotNull(result, "The result should not be null");
        assertEquals("comment1", result.getId(), "The comment ID should be 'comment1'");
        assertEquals("post1", result.getItemId(), "The item ID should be 'post1'");
        assertEquals("Test comment", result.getContent(), "The content should be 'Test comment'");
        assertEquals("user1", result.getUsername(), "The username should be 'user1'");
        assertNull(result.getParentCommentId(), "The parent comment ID should be null");
    }

    @Test
    @DisplayName("Should throw CommentNotFoundException when comment is not found")
    void getCommentById_notFound_throwsException() {
        when(commentRepository.findById("comment1")).thenReturn(Optional.empty());

        // Assert that the exception is thrown
        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById("comment1"));
    }

    @Test
    @DisplayName("Should list all comments by post ID")
    void listCommentsByPostId_success() {
        when(commentRepository.findByItemId("post1")).thenReturn(Collections.singletonList(comment));
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(new CommentDto("comment1", "post1", "Test comment", "user1", null, comment.getCreatedAt().toString()));

        // Call the service method
        List<CommentDto> comments = commentService.listCommentsByPostId("post1");

        // Verify the result
        assertNotNull(comments, "The comments list should not be null");
        assertEquals(1, comments.size(), "There should be 1 comment in the list");
        assertEquals("comment1", comments.get(0).getId(), "The comment ID should be 'comment1'");
        assertEquals("post1", comments.get(0).getItemId(), "The item ID should be 'post1'");
        assertEquals("Test comment", comments.get(0).getContent(), "The content should be 'Test comment'");
    }

    @Test
    @DisplayName("Should return empty list when no comments are found for the post")
    void listCommentsByPostId_noComments_returnsEmptyList() {
        when(commentRepository.findByItemId("post1")).thenReturn(Collections.emptyList());

        // Call the service method
        List<CommentDto> comments = commentService.listCommentsByPostId("post1");

        // Verify the result
        assertNotNull(comments, "The comments list should not be null");
        assertTrue(comments.isEmpty(), "The comments list should be empty");
    }

    @Test
    @DisplayName("Should successfully retrieve comments for a specific user")
    void getCommentForUserByUserId_success() {
        // Given
        Comment comment1 = new Comment();
        comment1.setId("comment1");
        comment1.setItemId("post1");
        comment1.setContent("Test comment 1");
        comment1.setUsername("user1");
        comment1.setParentCommentId(null);
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId("comment2");
        comment2.setItemId("post2");
        comment2.setContent("Test comment 2");
        comment2.setUsername("user1");
        comment2.setParentCommentId(null);
        comment2.setCreatedAt(LocalDateTime.now());

        // Mock the repository response
        when(commentRepository.findByUsername("user1")).thenReturn(List.of(comment1, comment2));

        // Mock ModelMapper mapping
        CommentDto commentDto1 = new CommentDto("comment1", "post1", "Test comment 1", "user1", null, comment1.getCreatedAt().toString());
        CommentDto commentDto2 = new CommentDto("comment2", "post2", "Test comment 2", "user1", null, comment2.getCreatedAt().toString());
        when(modelMapper.map(comment1, CommentDto.class)).thenReturn(commentDto1);
        when(modelMapper.map(comment2, CommentDto.class)).thenReturn(commentDto2);

        // Call the service method
        List<CommentDto> comments = commentService.getCommentForUserByUserId("user1");

        // Verify the result
        assertNotNull(comments, "The comments list should not be null");
        assertEquals(2, comments.size(), "There should be 2 comments for 'user1'");
        assertEquals("comment1", comments.get(0).getId(), "The first comment ID should be 'comment1'");
        assertEquals("comment2", comments.get(1).getId(), "The second comment ID should be 'comment2'");
    }

    @Test
    @DisplayName("Should return empty list when no comments are found for the user")
    void getCommentForUserByUserId_noComments_returnsEmptyList() {
        // Mock the repository response
        when(commentRepository.findByUsername("user1")).thenReturn(Collections.emptyList());

        // Call the service method
        List<CommentDto> comments = commentService.getCommentForUserByUserId("user1");

        // Verify the result
        assertNotNull(comments, "The comments list should not be null");
        assertTrue(comments.isEmpty(), "The comments list should be empty");
    }

}
