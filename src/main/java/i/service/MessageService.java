package i.service;

import i.dto.MessageDto;
import i.exception.MessageAccessDeniedException;
import i.model.Message;
import i.repository.MessageRepository;
import i.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Sends a message from one user to another.
     *
     * @param recipient The user who receives the message.
     * @param sender    The user who sends the message.
     * @param content   The content of the message.
     * @return MessageDto with the details of the sent message.
     */
    public MessageDto sendMessage(String recipient, String sender, String content) {
        log.debug("Sending message from {} to {}", sender, recipient);

        // Ensure the recipient exists
        if (!userRepository.existsByUsername(recipient)) {
            log.error("Recipient {} not found", recipient);
            throw new UsernameNotFoundException(String.format("Recipient %s not found", recipient));
        }

        // Ensure the sender exists
        if (!userRepository.existsByUsername(sender)) {
            log.error("Sender {} not found", sender);
            throw new UsernameNotFoundException(String.format("Sender %s not found", sender));
        }

        // Create a new message object and set its properties
        Message message = new Message();
        message.setRecipient(recipient);
        message.setSender(sender);
        message.setContent(content);

        // Save the message to the repository and get the saved message
        Message sentMessage = repository.save(message);

        log.info("Message sent successfully from {} to {}", sender, recipient);

        // Return the message details as a MessageResponseDto
        return modelMapper.map(sentMessage, MessageDto.class);
    }

    /**
     * Retrieves all messages sent by a specific sender.
     *
     * @param sender The sender whose messages are to be retrieved.
     * @return A list of messages sent by the specified sender.
     */
    public List<MessageDto> getMessagesBySender(String sender) {
        log.debug("Retrieving messages sent by {}", sender);

        // Ensure that the sender exists before retrieving their messages
        if (!userRepository.existsByUsername(sender)) {
            log.error("Sender {} not found", sender);
            throw new UsernameNotFoundException(String.format("Sender %s not found", sender));
        }

        List<MessageDto> messages = repository.findBySender(sender).stream()
                .map(message -> modelMapper.map(message, MessageDto.class))
                .toList();

        log.info("Retrieved {} messages sent by {}", messages.size(), sender);
        return messages;
    }

    /**
     * Retrieves all messages received by a specific recipient.
     *
     * @param recipient The recipient whose messages are to be retrieved.
     * @return A list of messages received by the specified recipient.
     */
    public List<MessageDto> getMessagesByRecipient(String recipient) {
        log.debug("Retrieving messages received by {}", recipient);

        // Ensure that the recipient exists before retrieving their messages
        if (!userRepository.existsByUsername(recipient)) {
            log.error("Recipient {} not found", recipient);
            throw new UsernameNotFoundException(String.format("Recipient %s not found", recipient));
        }

        List<MessageDto> messages = repository.findByRecipient(recipient).stream()
                .map(message -> modelMapper.map(message, MessageDto.class))
                .toList();

        log.info("Retrieved {} messages received by {}", messages.size(), recipient);
        return messages;
    }

    /**
     * Retrieves a specific message by its ID, ensuring the requesting user is either the sender or recipient.
     *
     * @param id   The ID of the message.
     * @param name The name of the user making the request.
     * @return The message details as a MessageDto.
     */
    public MessageDto getMessagesById(String id, String name) {
        log.debug("Retrieving message with ID: {} for user: {}", id, name);

        return repository.findById(id)
                .map(post -> {
                    // Check if the user is either the sender or the recipient
                    if (!post.getSender().equals(name) && !post.getRecipient().equals(name)) {
                        log.error("User {} does not have access to message ID: {}", name, id);
                        throw new MessageAccessDeniedException(
                                String.format("Recipient or sender of the message does not match with user %s", name));
                    }

                    log.info("Message with ID: {} retrieved successfully for user: {}", id, name);
                    return modelMapper.map(post, MessageDto.class);
                })
                .orElseThrow(() -> {
                    log.error("Message with ID: {} not found", id);
                    return new RuntimeException("Message not found");
                });
    }
}
