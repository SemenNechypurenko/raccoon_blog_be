package i.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateResponseDto {
    private String id;              // Comment ID
    private String postId;          // Post ID
    private String content;         // Comment text
    private String authorId;        // Author ID
    private String parentCommentId; // Parent comment ID, if any
    private String createdAt;       // Comment creation date
}