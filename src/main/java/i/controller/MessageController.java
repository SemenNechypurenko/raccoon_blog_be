package i.controller;

import i.dto.MessageDto;
import i.service.MessageService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static i.utils.UserUtils.getCurrentAuthUser;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageService messageService;

    // Endpoint for sending a message
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@RequestParam String recipient,
                                                  @RequestParam String content) {
        return ResponseEntity.ok(messageService.sendMessage(recipient, getCurrentAuthUser(), content)); // Return the DTO as the response
    }

    // Endpoint for retrieving sent messages
    @GetMapping("/sent")
    public ResponseEntity<List<MessageDto>> getSentMessages() {
        return ResponseEntity.ok(messageService.getMessagesBySender(getCurrentAuthUser()));
    }

    // Endpoint for retrieving received messages
    @GetMapping("/received")
    public ResponseEntity<List<MessageDto>> getReceivedMessages() {
        return ResponseEntity.ok(messageService.getMessagesByRecipient(getCurrentAuthUser()));
    }

    // Endpoint for retrieving messages by id
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable("id") String id) {
        return ResponseEntity.ok(messageService.getMessagesById(id, getCurrentAuthUser()));
    }
}
