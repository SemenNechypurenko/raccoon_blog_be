package i.dto;

import lombok.Data;

@Data
public class PostCreateResponseDto {
    private String id;          // Post ID
    private String title;       // Post title
    private String content;     // Post text
    private String username;    // Author username
    private String createdAt;   // Post creation date
    private String imageUrl;    // URL of the attached image
}
