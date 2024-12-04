package i.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    @Id
    private String id = UUID.randomUUID().toString();   // Unique comment identifier

    @NotNull(message = "Post ID cannot be null")
    @Indexed
    private String itemId;                              // ID of the post or message to which the comment refers

    @NotEmpty(message = "Content cannot be empty")
    private String content;                             // Content of the comment

    @NotEmpty(message = "Username cannot be empty")
    @Indexed
    private String username;                            // Username of the comment author (user reference)

    private String parentCommentId;                     // Parent comment ID if this is a reply to another comment

    private LocalDateTime createdAt = LocalDateTime.now(); // Date and time when the comment was created
    private String type; // type og comment (post, message e.g.)
}
