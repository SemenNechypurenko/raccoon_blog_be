package i.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public abstract class Item {

    @Id
    private String id = UUID.randomUUID().toString();      // Уникальный идентификатор

    private LocalDateTime createdAt = LocalDateTime.now(); // Дата и время создания

    private Set<String> commentIds = new HashSet<>();      // Список ID комментариев

    @NotEmpty(message = "Content cannot be empty")
    private String content;                               // Общий текстовый контент
}
