package com.example.felipebarino.firebaseauthdemo;

public class Device{
    private String id;
    private String nick;
    private boolean on;

    public Device(){
        this.id = "0000";
        this.nick = "inicial";
        this.turnOff();
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void turnOn() {
        this.on = true;
    }

    public void turnOff() {
        this.on = false;
    }

    public boolean isOn() {
        return this.on;
    }

}
