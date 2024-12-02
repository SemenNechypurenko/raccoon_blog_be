package i.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data // Lombok generates getters, setters, toString, equals, and hashCode
@AllArgsConstructor // Lombok generates a constructor with all fields
@NoArgsConstructor
public class MessageDto {
    private String id;          // Message ID
    private String sender;      // Sender username
    private String recipient;   // Recipient username
    private String content;     // Message content
    private LocalDateTime createdAt; // Timestamp of message creation
}
