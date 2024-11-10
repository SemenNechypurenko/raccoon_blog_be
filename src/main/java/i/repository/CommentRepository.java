package i.repository;

import i.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    // Method to get all comments for a specific post
    List<Comment> findByPostId(String postId);

    // Method for getting all responses to a comment by its ID
    List<Comment> findByParentCommentId(String parentCommentId);
}
