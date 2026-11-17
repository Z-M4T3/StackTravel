package hu.unideb.inf.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripCardDto {
    @NotNull
    private Integer tripId;

    @NotNull
    private Integer cardId;

    @NotNull
    private Integer cardDay;

    @Schema(example = "2026-11-11-11-51-00")
    private LocalDateTime startTime;

    private Integer duration;

}
