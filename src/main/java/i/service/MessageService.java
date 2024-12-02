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

    public MessageResponseDto sendMessage(String sender, String recipient, String content) {
        userRepository.userExists(sender);
        userRepository.userExists(recipient);

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        Message sentMessage = repository.save(message);

        return modelMapper.map(sentMessage, MessageResponseDto.class);
    }

    public List<Message> getMessagesBySender(String sender) {
        userRepository.userExists(sender);
        return repository.findBySender(sender);
    }

    public List<Message> getMessagesByRecipient(String recipient) {
        userRepository.userExists(recipient);
        return repository.findByRecipient(recipient);
    }
}
