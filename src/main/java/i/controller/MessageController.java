package i.controller;

import i.dto.MessageResponseDto;
import i.model.Message;
import i.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Marks this class as a REST controller
@RequestMapping("/messages") // All routes in this controller will start with "/api/messages"
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // Endpoint for sending a message
    @PostMapping
    public ResponseEntity<MessageResponseDto> sendMessage(@Valid @RequestParam String sender,
                                                          @RequestParam String recipient,
                                                          @RequestParam String content) {
        return ResponseEntity.ok(messageService.sendMessage(sender, recipient, content)); // Return the DTO as the response
    }

    // Endpoint for retrieving sent messages
    @GetMapping("/sent")
    public ResponseEntity<List<MessageResponseDto>> getSentMessages(@RequestParam String sender) {
        List<Message> messages = messageService.getMessagesBySender(sender);

        // Convert each message to DTO
        List<MessageResponseDto> responseDtos = messages.stream()
                .map(message -> new MessageResponseDto(
                        message.getId(),
                        message.getSender(),
                        message.getRecipient(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos); // Return list of DTOs as the response
    }

    // Endpoint for retrieving received messages
    @GetMapping("/received")
    public ResponseEntity<List<MessageResponseDto>> getReceivedMessages(@RequestParam String recipient) {
        List<Message> messages = messageService.getMessagesByRecipient(recipient);

        // Convert each message to DTO
        List<MessageResponseDto> responseDtos = messages.stream()
                .map(message -> new MessageResponseDto(
                        message.getId(),
                        message.getSender(),
                        message.getRecipient(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos); // Return list of DTOs as the response
    }
}
