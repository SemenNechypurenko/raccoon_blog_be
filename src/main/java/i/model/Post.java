package i.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Post {

    @Id
    private String id = UUID.randomUUID().toString();                  // Unique post identifier
    private String title;               // Post title
    private String content;             // Post text
    private String username;            // Post author username
    private LocalDateTime createdAt = LocalDateTime.now();    // Date and time the post was created
    private Set<String> tags = new HashSet<>();           // Post tags
    private Set<String> commentIds = new HashSet<>();     // List of comments (ID) belonging to this post
}

