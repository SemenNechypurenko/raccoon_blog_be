package i.repository;

import i.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByConfirmationToken(String confirmationToken);

    default void checkUserExists(String userId) {
        if (!existsById(userId))
            throw new UsernameNotFoundException(String.format("Username %s not found", userId));
    }
}

