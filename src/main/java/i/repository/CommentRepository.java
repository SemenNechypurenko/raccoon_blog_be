package i.repository;

import i.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    // Method to retrieve comments by Post ID
    List<Comment> findByItemId(String postId);
    // Method to retrieve comments by username
    List<Comment> findByUsername(String username);
}
