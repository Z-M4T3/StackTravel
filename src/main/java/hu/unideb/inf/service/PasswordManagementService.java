package hu.unideb.inf.service;

import hu.unideb.inf.email.PasswordResetEmailSender;
import hu.unideb.inf.exceptions.IncorrectPasswordException;
import hu.unideb.inf.exceptions.InvalidPasswordResetTokenException;
import hu.unideb.inf.exceptions.UserNotFoundException;
import hu.unideb.inf.model.entity.PasswordResetToken;
import hu.unideb.inf.model.entity.User;
import hu.unideb.inf.repository.PasswordResetTokenRepository;
import hu.unideb.inf.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@Transactional
public class PasswordManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordManagementService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Duration RESET_TOKEN_VALIDITY = Duration.ofMinutes(30);
    private static final int RESET_TOKEN_BYTES = 32;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetEmailSender passwordResetEmailSender;
    private final MessageSource messageSource;

    public PasswordManagementService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            PasswordResetEmailSender passwordResetEmailSender,
            MessageSource messageSource
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetEmailSender = passwordResetEmailSender;
        this.messageSource = messageSource;
    }

    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        message("user.not-found", userId)
                ));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IncorrectPasswordException(message("password.current.incorrect"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.deleteAllByUserId(user.getId());
    }

    public void resetPassword(String rawToken, String newPassword) {
        if (rawToken == null || rawToken.isBlank()) {
            throw invalidPasswordResetToken();
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    message("password.reset.new-password.required")
            );
        }

        LocalDateTime now = LocalDateTime.now();
        String tokenHash = hashResetToken(rawToken);
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenHashAndExpiresAtAfter(tokenHash, now)
                .orElseThrow(this::invalidPasswordResetToken);

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(this::invalidPasswordResetToken);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.deleteAllByUserId(user.getId());
    }

    public String requestPasswordReset(String email) {
        if (email != null && !email.isBlank()) {
            userRepository.findByEmail(email).ifPresent(user -> {
                String rawToken = createRawResetToken();
                PasswordResetToken resetToken = new PasswordResetToken(
                        hashResetToken(rawToken),
                        user.getId(),
                        LocalDateTime.now().plus(RESET_TOKEN_VALIDITY)
                );

                passwordResetTokenRepository.deleteAllByUserId(user.getId());
                passwordResetTokenRepository.save(resetToken);

                try {
                    passwordResetEmailSender.sendResetLink(user.getEmail(), rawToken);
                } catch (MailException exception) {
                    LOGGER.error(
                            "Failed to send password reset email for user ID {}",
                            user.getId(),
                            exception
                    );
                }
            });
        }

        return message("password.reset.request.accepted");
    }

    private String createRawResetToken() {
        byte[] tokenBytes = new byte[RESET_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashResetToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(message("security.sha256.unavailable"), exception);
        }
    }

    private InvalidPasswordResetTokenException invalidPasswordResetToken() {
        return new InvalidPasswordResetTokenException(
                message("password.reset.token.invalid")
        );
    }

    private String message(String code, Object... arguments) {
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }
}
