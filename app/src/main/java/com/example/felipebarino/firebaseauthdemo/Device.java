package com.example.felipebarino.firebaseauthdemo;

import android.text.TextUtils;

public class Device{

    private String nick;
    private String state;

    private String on = "ON";
    private String off = "OFF";

    public Device(String nick){
        this.nick = nick;
        this.turnOff();
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void turnOn() {
        setState(this.on);
    }

    public void turnOff() {
        setState(this.off);
    }

    public boolean isOn() {
        if(TextUtils.equals(this.state, this.on)){
            return true;
        }else if (TextUtils.equals(this.state, this.off)){
            return false;
        }else return false;
    }

}
