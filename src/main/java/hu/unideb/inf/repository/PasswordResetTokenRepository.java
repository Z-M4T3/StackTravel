package hu.unideb.inf.repository;

import hu.unideb.inf.model.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByTokenHashAndExpiresAtAfter(
            String tokenHash,
            LocalDateTime now
    );

    void deleteAllByUserId(Integer userId);
}
