package i.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequestDto {
    private String postId;           // ID of the post to which the comment refers
    private String content;          // Comment text
    private String authorId;         // ID of the comment author
    private String parentCommentId;  // Parent comment ID, if any
}