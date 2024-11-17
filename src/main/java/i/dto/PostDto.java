package i.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class PostDto {
    private String id;                  // Unique post identifier
    private String title;               // Post title
    private String content;             // Post text
    private String username;            // Post author username
    private LocalDateTime createdAt;  // Date and time the post was created
    private Set<String> tags = new HashSet<>();           // Post tags
    private Set<String> commentIds = new HashSet<>(); // Post comment Ids
}
