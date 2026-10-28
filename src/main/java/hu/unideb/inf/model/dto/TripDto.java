package hu.unideb.inf.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    @PositiveOrZero
    private Integer index;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer userId;
    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer cardId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
