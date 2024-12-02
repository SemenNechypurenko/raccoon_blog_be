package i.repository;

import i.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySender(String sender);       // Поиск сообщений по отправителю
    List<Message> findByRecipient(String recipient); // Поиск сообщений по получателю
}
