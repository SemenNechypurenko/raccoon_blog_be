package i.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {

    @Id
    private String id = UUID.randomUUID().toString();
    private String name;

    public Role(String name) {
        this.name = name;
    }
}