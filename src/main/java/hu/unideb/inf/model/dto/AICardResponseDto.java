package hu.unideb.inf.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AICardResponseDto {
    private String name;
    private String description;
    private String category;
    private String location;
    private String webpage;
    private String duration;
}