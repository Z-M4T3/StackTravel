package hu.unideb.inf.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    @Size(max = 255)
    @Schema(example = "demo-user")
    private String username;
    @Email
    @Size(max = 255)
    @Schema(example = "demo@example.com")
    private String email;
    @Past
    @Schema(example = "2000-01-01")
    private LocalDate birthDate;
    @Size(max = 255)
    @Schema(example = "avatar.png")
    private String picture;
}
