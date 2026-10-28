package hu.unideb.inf.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    @NotBlank
    @Schema(example = "demo-user")
    private String username;
    @NotBlank
    @Email
    @Schema(example = "demo@example.com")
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Schema(example = "DemoPassword123!")
    private String password;
    @Past
    @Schema(example = "2000-01-01")
    private LocalDate birthDate;
    @Schema(example = "avatar.png")
    private String picture;
}
