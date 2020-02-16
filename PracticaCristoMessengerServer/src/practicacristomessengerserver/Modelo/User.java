/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicacristomessengerserver.Modelo;
/**
 *
 * @author Matt Workstation
 */
public class User {
    private String id;
    private String password;
    private String name;
    private String firstSurname;
    private String secondSurname;
    private String photo;
    private boolean state;

    public User(String id, String password, String name, String firstSurname, String secondSurname, String photo, boolean state) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.firstSurname = firstSurname;
        this.secondSurname = secondSurname;
        this.photo = photo;
        this.state = state;
    }

    public User() {
        this.id = "";
        this.password = "";
        this.name = "";
        this.firstSurname = "";
        this.secondSurname = "";
        this.photo = "";
        this.state = false;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = secondSurname;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public String getSecondSurname() {
        return secondSurname;
    }

    public String getPhoto() {
        return photo;
    }

    public boolean getState() {
        return state;
    }
    
}
