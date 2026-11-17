package hu.unideb.inf.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripCard {
    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cardId")
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "card_day", nullable = false)
    private Integer cardDay;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "duration")
    private Integer duration;

    @Embeddable
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @EqualsAndHashCode
    public static class Id implements Serializable {
        @Column(name = "trip_id", nullable = false)
        private Integer tripId;

        @Column(name = "card_id", nullable = false)
        private Integer cardId;
    }
}
