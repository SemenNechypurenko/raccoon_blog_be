package i.dto;

import lombok.Data;

@Data
public class CommentCreateRequestDto {
    private String postId;          // ID поста, к которому относится комментарий
    private String content;         // Текст комментария
    private String parentCommentId; // ID родительского комментария, если это ответ на другой комментарий
}
