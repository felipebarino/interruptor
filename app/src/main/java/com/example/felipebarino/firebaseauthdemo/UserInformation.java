package com.example.felipebarino.firebaseauthdemo;

public class UserInformation {
    private String name;
    private String lastname;

    public UserInformation(String name, String lastname){
        this.name = name;
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setName(String name) {
        this.name = name;
    }
}
