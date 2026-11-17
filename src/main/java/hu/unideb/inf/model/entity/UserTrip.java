package hu.unideb.inf.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "trip_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTrip {
    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    @JoinColumn(name = "trip_id")
    private Trip trip;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "permission_level", nullable = false)
    private String permissionLevel;

    @Embeddable
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @EqualsAndHashCode
    public static class Id implements Serializable {
        @Column(name = "trip_id", nullable = false)
        private Integer tripId;

        @Column(name = "user_id", nullable = false)
        private Integer userId;
    }
}
