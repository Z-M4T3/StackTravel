package hu.unideb.inf.model.dto;

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
    private Integer id;
    private Integer index;
    private Integer userId;
    private Integer cardId;
    private LocalDateTime createdAt;
}