package hu.unideb.inf.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank
    @Schema(description = "Current password", example = "DemoPassword123!")
    private String password;
    @NotBlank
    @Schema(example = "NewPassword123!")
    private String newPassword;

    @Override
    public String toString() {
        return "ChangePasswordRequest[password=[REDACTED], newPassword=[REDACTED]]";
    }
}
