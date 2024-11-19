package i.dto;

import lombok.Data;

@Data
public class CommentDto {
    private String id;
    private String postId;
    private String content;
    private String authorId;
    private String parentCommentId;
    private String createdAt;
}
