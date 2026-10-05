package hu.unideb.inf.models;

import java.time.LocalDate;
import java.util.Date;

public class UserModel {
    //User (id, username, email, pass, age, picture, lastemail, lastemailchange, lastlogin)
    int id;
    String userName, email, pass, age, lastEmail;
    LocalDate lastLogin,LastEmailChange;
    // milyen típus legyen a picture?

    public UserModel(int id, String userName, String email, String pass, String age, String lastEmail, LocalDate lastLogin, LocalDate lastEmailChange) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.pass = pass;
        this.age = age;
        this.lastEmail = lastEmail;
        this.lastLogin = lastLogin;
        LastEmailChange = lastEmailChange;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getAge() {
        return age;
    }

    public String getLastEmail() {
        return lastEmail;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public LocalDate getLastEmailChange() {
        return LastEmailChange;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setLastEmail(String lastEmail) {
        this.lastEmail = lastEmail;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastEmailChange(LocalDate lastEmailChange) {
        LastEmailChange = lastEmailChange;
    }

}
