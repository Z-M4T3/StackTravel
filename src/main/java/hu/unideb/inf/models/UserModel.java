package hu.unideb.inf.models;

public class UserModel {
    int Id;
    String UserName, descripition,location,destination;
    // ez milyen típus? picture;


    public UserModel(int id, String userName, String descripition, String location, String destination) {
        Id = id;
        UserName = userName;
        this.descripition = descripition;
        this.location = location;
        this.destination = destination;
    }

    public int getId() {
        return Id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
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
