package hu.unideb.inf.security;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    // log_rounds
    private static final int WORKLOAD = 12;

    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("A jelszó nem lehet üres!");
        }
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(rawPassword, salt);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}