package i.service;

import i.dto.MessageDto;
import i.exception.MessageAccessDeniedException;
import i.model.Message;
import i.repository.MessageRepository;
import i.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MessageService messageService;

    private Message testMessage;
    private MessageDto testMessageDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample test message and DTO
        testMessage = new Message();
        testMessage.setId("1");
        testMessage.setSender("sender");
        testMessage.setRecipient("recipient");
        testMessage.setContent("Hello, world!");
        testMessage.setCreatedAt(LocalDateTime.now());
        testMessage.setCommentIds(new HashSet<>());
        testMessageDto = new MessageDto("1", "sender", "recipient", "Hello, world!", LocalDateTime.now(), null);
    }

    @Test
    @Description("Should return a MessageDto when the sender and recipient are valid.")
    void sendMessage_ShouldReturnMessageDto_WhenValidSenderAndRecipient() {
        // Arrange
        when(userRepository.existsByUsername("sender")).thenReturn(true);
        when(userRepository.existsByUsername("recipient")).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(modelMapper.map(testMessage, MessageDto.class)).thenReturn(testMessageDto);

        // Act
        MessageDto result = messageService.sendMessage("recipient", "sender", "Hello, world!");

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getId());
        Assertions.assertEquals("sender", result.getSender());
        Assertions.assertEquals("recipient", result.getRecipient());
        Assertions.assertEquals("Hello, world!", result.getContent());
    }

    @Test
    @Description("Should throw an exception when the sender is not found.")
    void sendMessage_ShouldThrowException_WhenSenderNotFound() {
        // Arrange
        when(userRepository.existsByUsername("sender")).thenReturn(false);
        when(userRepository.existsByUsername("recipient")).thenReturn(true);

        // Act & Assert
        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> messageService.sendMessage("recipient", "sender", "Hello, world!"));
        Assertions.assertEquals("Sender sender not found", exception.getMessage());
    }

    @Test
    @Description("Should throw an exception when the recipient is not found.")
    void sendMessage_ShouldThrowException_WhenRecipientNotFound() {
        // Arrange
        when(userRepository.existsByUsername("sender")).thenReturn(true);
        when(userRepository.existsByUsername("recipient")).thenReturn(false);

        // Act & Assert
        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> messageService.sendMessage("recipient", "sender", "Hello, world!"));
        Assertions.assertEquals("Recipient recipient not found", exception.getMessage());
    }

    @Test
    @Description("Should return a list of messages sent by the sender.")
    void getMessagesBySender_ShouldReturnMessageList_WhenSenderExists() {
        // Arrange
        when(userRepository.existsByUsername("sender")).thenReturn(true);
        when(messageRepository.findBySender("sender")).thenReturn(List.of(testMessage));
        when(modelMapper.map(testMessage, MessageDto.class)).thenReturn(testMessageDto);

        // Act
        List<MessageDto> result = messageService.getMessagesBySender("sender");

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1", result.get(0).getId());
    }

    @Test
    @Description("Should throw an exception when the sender is not found while fetching messages.")
    void getMessagesBySender_ShouldThrowException_WhenSenderNotFound() {
        // Arrange
        when(userRepository.existsByUsername("sender")).thenReturn(false);

        // Act & Assert
        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> messageService.getMessagesBySender("sender"));
        Assertions.assertEquals("Sender sender not found", exception.getMessage());
    }

    @Test
    @Description("Should return a list of messages received by the recipient.")
    void getMessagesByRecipient_ShouldReturnMessageList_WhenRecipientExists() {
        // Arrange
        when(userRepository.existsByUsername("recipient")).thenReturn(true);
        when(messageRepository.findByRecipient("recipient")).thenReturn(List.of(testMessage));
        when(modelMapper.map(testMessage, MessageDto.class)).thenReturn(testMessageDto);

        // Act
        List<MessageDto> result = messageService.getMessagesByRecipient("recipient");

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1", result.get(0).getId());
    }

    @Test
    @Description("Should throw an exception when the recipient is not found while fetching messages.")
    void getMessagesByRecipient_ShouldThrowException_WhenRecipientNotFound() {
        // Arrange
        when(userRepository.existsByUsername("recipient")).thenReturn(false);

        // Act & Assert
        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> messageService.getMessagesByRecipient("recipient"));
        Assertions.assertEquals("Recipient recipient not found", exception.getMessage());
    }

    @Test
    @Description("Should return a MessageDto for a given message ID when user has access.")
    void getMessagesById_ShouldReturnMessageDto_WhenUserHasAccess() {
        // Arrange
        when(messageRepository.findById("1")).thenReturn(Optional.of(testMessage));
        when(modelMapper.map(testMessage, MessageDto.class)).thenReturn(testMessageDto);

        // Act
        MessageDto result = messageService.getMessagesById("1", "sender");

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getId());
    }

    @Test
    @Description("Should throw an exception when the user does not have access to the message.")
    void getMessagesById_ShouldThrowException_WhenUserDoesNotHaveAccess() {
        // Arrange
        testMessage.setSender("anotherUser");
        when(messageRepository.findById("1")).thenReturn(Optional.of(testMessage));

        // Act & Assert
        MessageAccessDeniedException exception = Assertions.assertThrows(MessageAccessDeniedException.class, () -> messageService.getMessagesById("1", "sender"));
        Assertions.assertEquals("Recipient or sender of the message does not match with user sender", exception.getMessage());
    }

    @Test
    @Description("Should throw an exception when the message is not found by its ID.")
    void getMessagesById_ShouldThrowException_WhenMessageNotFound() {
        // Arrange
        when(messageRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> messageService.getMessagesById("1", "sender"));
        Assertions.assertEquals("Message not found", exception.getMessage());
    }
}