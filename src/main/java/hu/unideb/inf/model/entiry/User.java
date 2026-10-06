package hu.unideb.inf.model.entiry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    //User (id, username, email, pass, age, picture, lastemail, lastemailchange, lastlogin)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "pass", nullable = false)
    private String pass;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "last_email")
    private String lastEmail;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "last_email_change")
    private LocalDateTime lastEmailChange;

    @Column(name = "picture")
    private String picture;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void setCreatedAtIfMissing() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}