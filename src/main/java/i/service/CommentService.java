package i.service;

import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.model.Comment;
import i.repository.CommentRepository;
import i.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public CommentCreateResponseDto addComment(CommentCreateRequestDto commentDto) {
        // Check that the post exists
        postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // If parentCommentId is specified, check that the parent comment exists
        if (commentDto.getParentCommentId() != null) {
            commentRepository.findById(commentDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
        }

        // Convert CommentCreateRequestDto to Comment
        Comment comment = convertToEntity(commentDto);
        comment = commentRepository.save(comment);

        // Convert the saved Comment to CommentCreateResponseDto
        return convertFromEntity(comment);
    }

    /**
     * Convert CommentCreateRequestDto to Comment using ModelMapper.
     */
    private Comment convertToEntity(CommentCreateRequestDto commentDto) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setCreatedAt(java.time.LocalDateTime.now());
        return comment;
    }

    /**
     * Convert Comment to CommentCreateResponseDto.
     */
    private CommentCreateResponseDto convertFromEntity(Comment comment) {
        return modelMapper.map(comment, CommentCreateResponseDto.class);
    }
}
