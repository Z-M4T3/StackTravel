package hu.unideb.inf.models;

public class CardModel {
    int id;
    String name, descripition,location,destination;
    // ez milyen típus? picture;


    public CardModel(int id, String name, String descripition, String location, String destination) {
        id = id;
        name = name;
        this.descripition = descripition;
        this.location = location;
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        name = name;
    }

    public String getDescripition() {
        return descripition;
    }

    public void setDescripition(String descripition) {
        this.descripition = descripition;
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
}
