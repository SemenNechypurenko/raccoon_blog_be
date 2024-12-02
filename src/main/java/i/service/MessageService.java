package i.service;

import i.dto.MessageResponseDto;
import i.model.Message;
import i.repository.MessageRepository;
import i.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Sends a message from one user to another.
     *
     * @param recipient The user who receives the message.
     * @param content   The content of the message.
     * @return MessageResponseDto with the details of the sent message.
     */
    public MessageResponseDto sendMessage(String recipient, String sender, String content) {
        userRepository.checkUserExists(recipient);  // Throws an exception if the recipient doesn't exist
        // Create a new message object and set its properties
        Message message = new Message();
        message.setRecipient(recipient);
        message.setSender(sender);
        message.setContent(content);
        // Save the message to the repository and get the saved message
        Message sentMessage = repository.save(message);
        // Return the message details as a MessageResponseDto
        return modelMapper.map(sentMessage, MessageResponseDto.class);
    }

    /**
     * Retrieves all messages sent by a specific sender.
     *
     * @param sender The sender whose messages are to be retrieved.
     * @return A list of messages sent by the specified sender.
     */
    public List<Message> getMessagesBySender(String sender) {
        // Ensure that the sender exists before retrieving their messages
        userRepository.checkUserExists(sender);
        return repository.findBySender(sender);  // Fetch messages sent by the sender
    }

    /**
     * Retrieves all messages received by a specific recipient.
     *
     * @param recipient The recipient whose messages are to be retrieved.
     * @return A list of messages received by the specified recipient.
     */
    public List<Message> getMessagesByRecipient(String recipient) {
        // Ensure that the recipient exists before retrieving their messages
        userRepository.checkUserExists(recipient);
        return repository.findByRecipient(recipient);  // Fetch messages received by the recipient
    }
}
