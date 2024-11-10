package i.dto;

import lombok.Data;

@Data
public class PostCreateResponseDto {
    private String id;          // Post ID
    private String title;       // Post title
    private String content;     // Post text
    private String authorId;    // Author ID
    private String createdAt;   // Post creation date
}