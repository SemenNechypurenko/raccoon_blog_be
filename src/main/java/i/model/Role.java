package i.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Document(collection = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {

    @Id
    private String id = UUID.randomUUID().toString();

    @NotEmpty(message = "Role name cannot be empty")
    @Indexed(unique = true)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
