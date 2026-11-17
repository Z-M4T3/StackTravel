package hu.unideb.inf.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCardRequest {
    private Integer cardId;        // If card exists
    private CardDto newCard;       // AI generated - to be saved

    @NotNull
    private Integer cardDay;
    private LocalDateTime startTime;
    private Integer duration;
}