package hu.unideb.inf.models;
import jakarta.persistence.*;

@Entity
@Table(name = "card")   // ide írd az adatbázis tábla nevét

public class CardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "destination")
    private String destination;

    @Column(name = "picture")
    private String picture;

    public CardModel() {
    }

    public CardModel(String name, String description, String location, String destination, String picture) {
        //this.id = id;             Ezt elvileg az adatbázis generálja
        this.name = name;
        this.description = description;
        this.location = location;
        this.destination = destination;
        this.picture=picture;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
