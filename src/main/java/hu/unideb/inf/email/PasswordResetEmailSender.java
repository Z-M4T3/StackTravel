package hu.unideb.inf.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class PasswordResetEmailSender {

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;
    private final String from;
    private final String resetPageUrl;

    public PasswordResetEmailSender(
            JavaMailSender mailSender,
            MessageSource messageSource,
            @Value("${app.mail.from}") String from,
            @Value("${app.password-reset.url}") String resetPageUrl
    ) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
        this.from = from;
        this.resetPageUrl = resetPageUrl;
    }

    public void sendResetLink(String recipient, String rawToken) {
        Locale locale = LocaleContextHolder.getLocale();
        String token = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String resetLink = resetPageUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject(messageSource.getMessage(
                "password.reset.subject",
                null,
                locale
        ));
        message.setText(messageSource.getMessage(
                "password.reset.body",
                new Object[]{resetLink},
                locale
        ));

        mailSender.send(message);
    }
}
