package hu.unideb.inf.models;

public class CardModel {
    private int id;
    private String name, descripition,location,destination;
    private int[][] picture;


    public CardModel(int id, String name, String descripition, String location, String destination,int[][] picture) {
        this.id = id;
        this.name = name;
        this.descripition = descripition;
        this.location = location;
        this.destination = destination;
        this.picture=picture;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public int[][] getPicture() {
        return picture;
    }

    public void setPicture(int[][] picture) {
        this.picture = picture;
    }
}
