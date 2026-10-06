package hu.unideb.inf.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "users")
public class UserModel {
    //User (id, username, email, pass, age, picture, lastemail, lastemailchange, lastlogin)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "pass", nullable = false)
    private String pass;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "last_email")
    private String lastEmail;

    @Column(name = "last_login")
    private LocalDate lastLogin;

    @Column(name = "last_email_change")
    private LocalDate lastEmailChange;

    @Column(name = "picture")
    private String picture;

    // JPA-hoz kötelező
    public UserModel() {}

    public UserModel(String userName, String email, String pass, LocalDate BirthDate, String lastEmail, LocalDate lastLogin, LocalDate lastEmailChange, String picture) {
        //this.id = id;         adatbázis generálja
        this.userName = userName;
        this.email = email;
        this.pass = pass;
        this.birthDate = BirthDate;
        this.lastEmail = lastEmail;
        this.lastLogin = lastLogin;
        this.lastEmailChange = lastEmailChange;
        this.picture=picture;
    }

    // Segédmetódus a számoláshoz
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    public Integer getId() {
        return id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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
    @Transient
    public int getAge() {
        return calculateAge(this.birthDate);
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
        this.birthDate = birthDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
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
