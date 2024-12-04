package i.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating a new comment.
 * Contains information about the Post/message, the comment content, and optionally a parent comment if it's a reply.
 */
@Data
public class CommentCreateRequestDto {

    /**
     * The ID of the Post/message to which the comment is related.
     * This field is required.
     */
    @NotBlank(message = "Post/message ID cannot be empty")
    private String itemId;

    /**
     * The content of the comment.
     * This field is required and should not be empty.
     */
    @NotBlank(message = "Content cannot be empty")
    private String content;

    /**
     * The ID of the parent comment if this is a reply to another comment.
     * This field can be null if the comment is not a reply.
     */
    private String parentCommentId;
}
