package i.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message extends Item {
    @NotEmpty(message = "Sender username cannot be empty")
    @Indexed
    private String sender;              // Имя пользователя отправителя

    @NotEmpty(message = "Recipient username cannot be empty")
    @Indexed
    private String recipient;           // Имя пользователя получателя
}
