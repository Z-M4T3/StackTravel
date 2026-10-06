package hu.unideb.inf.models;

import jakarta.persistence.*;

@Entity
@Table(name = "trip")   // ide írd be a tábla nevét

public class TripModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "indx")
    private int index;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "card_id")
    private Integer cardId;

    public TripModel() {}

    public TripModel(int index, Integer userId, Integer cardId) {
        //this.id = id;             adatbáis generálja
        this.index = index;
        this.userId = userId;
        this.cardId = cardId;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getId() {
        return id;
    }

    public int getIndx() {
        return index;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getCardId() {
        return cardId;
    }
}

