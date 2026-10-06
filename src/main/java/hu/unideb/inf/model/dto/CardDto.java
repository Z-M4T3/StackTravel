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
public class CardDto {
    private Integer id;
    private String name;
    private String description;
    private String location;
    private String destination;
    private String picture;
    private LocalDateTime createdAt;
}