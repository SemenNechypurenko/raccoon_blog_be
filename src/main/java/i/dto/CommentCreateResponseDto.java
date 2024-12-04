package i.dto;

import lombok.Data;

@Data
public class CommentCreateResponseDto {
    private String id;              // ID комментария
    private String itemId;          // ID поста
    private String content;         // Текст комментария
    private String username;        // ID автора
    private String parentCommentId; // ID родительского комментария
    private String createdAt;       // Дата создания
}
