package com.example.felipebarino.firebaseauthdemo;

import android.text.TextUtils;

public class UserDevice {
    private String id;
    private String nick;
    private String state;

    public String ON = "ON";
    public String OFF = "OFF";

    public UserDevice(String id, String nick, String state){
        this.id = id;
        this.nick = nick;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public boolean isOn() {
        if(TextUtils.equals(this.state, this.ON)){
            return true;
        }else if (TextUtils.equals(this.state, this.OFF)){
            return false;
        }else return false;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void turnON() {
        this.state = this.ON;
    }

    public void turnOFF() {
        this.state = this.OFF;
    }
}
