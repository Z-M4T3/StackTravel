package hu.unideb.inf.models;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class UserModel {
    //User (id, username, email, pass, age, picture, lastemail, lastemailchange, lastlogin)
    private int id;

    private String userName, email, pass, lastEmail;
    private LocalDate lastLogin,lastEmailChange, BirthDate;
    // milyen típus legyen a picture?

    public UserModel(int id, String userName, String email, String pass, LocalDate BirthDate, String lastEmail, LocalDate lastLogin, LocalDate lastEmailChange) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.pass = pass;

        this.lastEmail = lastEmail;
        this.lastLogin = lastLogin;
        this.lastEmailChange = lastEmailChange;
    }

    // Segédmetódus a számoláshoz
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
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

    public int getAge() {
        return calculateAge(this.BirthDate);
    }

    public String getLastEmail() {
        return lastEmail;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public LocalDate getLastEmailChange() {
        return lastEmailChange;
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

    public void setBirthDate(LocalDate birthDate) {
        BirthDate = birthDate;
    }

    public LocalDate getBirthDate() {
        return BirthDate;
    }

    public void setLastEmail(String lastEmail) {
        this.lastEmail = lastEmail;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastEmailChange(LocalDate lastEmailChange) {
        this.lastEmailChange = lastEmailChange;
    }

}
