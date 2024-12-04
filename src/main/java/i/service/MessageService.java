package i.service;

import i.dto.MessageDto;
import i.exception.MessageAccessDeniedException;
import i.model.Message;
import i.repository.MessageRepository;
import i.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public MessageDto sendMessage(String recipient, String sender, String content) {
        if (!userRepository.existsByUsername(recipient)) {
            throw new UsernameNotFoundException(String.format("recipient %s not found", recipient));
        }
        if (!userRepository.existsByUsername(sender)) {
            throw new UsernameNotFoundException(String.format("sender %s not found", sender));
        }
        // Create a new message object and set its properties
        Message message = new Message();
        message.setRecipient(recipient);
        message.setSender(sender);
        message.setContent(content);
        // Save the message to the repository and get the saved message
        Message sentMessage = repository.save(message);
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
        // Ensure that the sender exists before retrieving their messages
        if (!userRepository.existsByUsername(sender)) {
            throw new UsernameNotFoundException(String.format("sender %s not found", sender));
        }
        return repository.findBySender(sender).stream().map(message ->
                modelMapper.map(message, MessageDto.class)).toList();
    }

    /**
     * Retrieves all messages received by a specific recipient.
     *
     * @param recipient The recipient whose messages are to be retrieved.
     * @return A list of messages received by the specified recipient.
     */
    public List<MessageDto> getMessagesByRecipient(String recipient) {
        // Ensure that the recipient exists before retrieving their messages
        if (!userRepository.existsByUsername(recipient)) {
            throw new UsernameNotFoundException(String.format("recipient %s not found", recipient));
        }
        return repository.findByRecipient(recipient).stream().map(message ->
                modelMapper.map(message, MessageDto.class)).toList();
    }

    public MessageDto getMessagesById(String id, String name) {
        return repository.findById(id)
                .map(post -> {
                            if (!post.getSender().equals(name) && !post.getRecipient().equals(name)) {
                                throw new MessageAccessDeniedException(
                                        String.format("Recipient or sender of the message " +
                                                "does not match with user %s", name));
                            }
                            return modelMapper.map(post, MessageDto.class);
                        }
                ).orElseThrow(() -> new RuntimeException("Post not found"));
    }

}
