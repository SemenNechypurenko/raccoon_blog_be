package i.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // Inject the email address from configuration
    @Value("${spring.mail.username}")
    private String senderEmail;

    // Inject the confirmation URL template from the configuration or environment
    @Value("${mail.confirmation.template}")
    private String confirmationUrlTemplate;

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
        log.debug("Preparing to send email to: {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(senderEmail);

        try {
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    /**
     * Sends a confirmation email with a confirmation token.
     *
     * @param email             the recipient's email address
     * @param confirmationToken the confirmation token to be included in the email
     */
    public void sendConfirmationEmail(String email, String confirmationToken) {
        log.debug("Generating confirmation email for: {}", email);

        // Generate confirmation URL using the injected template
        final String confirmationUrl = confirmationUrlTemplate + "/users/confirm-email?token=" + confirmationToken;

        // Generate the email content
        final String text = CONFIRMATION_EMAIL_TEXT_TEMPLATE + confirmationUrl;

        // Use the generic email-sending method
        sendEmail(email, CONFIRMATION_EMAIL_SUBJECT, text);

        log.info("Confirmation email sent to: {}", email);
    }
}
