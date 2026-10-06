package hu.unideb.inf.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String email;
    private String usernameOrEmail;
    private String password;
    private String newPassword;
    private LocalDate birthDate;
    private String picture;
}
