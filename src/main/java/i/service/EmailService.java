package i.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Inject email address from configuration
    @Value("${spring.mail.username}")
    private String senderEmail;

    // Global constants
    private static final String CONFIRMATION_URL_TEMPLATE = "http://localhost:8080/api/users/confirm-email?token=";
    private static final String CONFIRMATION_EMAIL_SUBJECT = "Confirm your email address";
    private static final String CONFIRMATION_EMAIL_TEXT_TEMPLATE = "Please click the following link to confirm your email address: ";

    /**
     * Sends an email with the specified parameters.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param text    the content of the email
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(senderEmail);
        mailSender.send(message);
    }

    /**
     * Sends a confirmation email with a confirmation token.
     *
     * @param email             the recipient's email address
     * @param confirmationToken the confirmation token to be included in the email
     */
    public void sendConfirmationEmail(String email, String confirmationToken) {
        // Generate confirmation URL
        final String confirmationUrl = CONFIRMATION_URL_TEMPLATE + confirmationToken;

        // Generate email content
        final String text = CONFIRMATION_EMAIL_TEXT_TEMPLATE + confirmationUrl;

        // Use the generic email-sending method
        sendEmail(email, CONFIRMATION_EMAIL_SUBJECT, text);
    }
}
