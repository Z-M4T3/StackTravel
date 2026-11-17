package hu.unideb.inf.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTripDto {
    @NotNull
    private Integer tripId;

    @NotNull
    private Integer userId;

    @NotBlank
    @Pattern(regexp = "OWNER|EDITOR|VIEWER")
    private String permissionLevel;
}
