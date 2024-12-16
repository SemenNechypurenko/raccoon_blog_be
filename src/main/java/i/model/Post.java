package i.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Post extends Item {
    @Indexed
    private String title;               // Заголовок поста
    @Indexed
    private String username;            // Имя пользователя автора

    private Set<String> tags = new HashSet<>();  // Теги поста

    private String imageUrl;            // URL изображения
}
