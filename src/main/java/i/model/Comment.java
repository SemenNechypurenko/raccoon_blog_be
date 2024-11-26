package i.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    @Id
    private String id = UUID.randomUUID().toString();
    private String postId;  // ID of the post to which the comment refers
    private String content;
    private String username; // ID of the comment author (user link)
    private String parentCommentId; // Parent comment ID if this is a reply to another comment
    private LocalDateTime createdAt;
}

