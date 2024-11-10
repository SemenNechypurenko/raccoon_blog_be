package i.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    private String id = UUID.randomUUID().toString();

    private String username;
    private String password;
    private String email;
    private Set<Role> roles = new HashSet<>();

}