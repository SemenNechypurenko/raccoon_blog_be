package i.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {

    @Id
    private String id = UUID.randomUUID().toString();   // Уникальный идентификатор сообщения

    @NotEmpty(message = "Sender username cannot be empty")
    @Indexed
    private String sender;                              // Имя пользователя отправителя

    @NotEmpty(message = "Recipient username cannot be empty")
    @Indexed
    private String recipient;                           // Имя пользователя получателя

    @NotEmpty(message = "Message content cannot be empty")
    private String content;                             // Содержимое сообщения

    private LocalDateTime createdAt = LocalDateTime.now(); // Дата и время отправки сообщения
}
