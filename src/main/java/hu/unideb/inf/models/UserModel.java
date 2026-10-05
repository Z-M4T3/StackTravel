package hu.unideb.inf.models;

public class UserModel {
    int id;
    String userName, descripition,location,destination;
    // ez milyen típus? picture;


    public UserModel(int id, String userName, String descripition, String location, String destination) {
        id = id;
        userName = userName;
        this.descripition = descripition;
        this.location = location;
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = userName;
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
