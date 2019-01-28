package com.wat.student.adkoch.wattable.db.data.entities;

import java.io.Serializable;

public final class Subscription implements Serializable {
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;


    public String getTitle() {
        return title;
    }

    public String getToken() {
        return token;
    }



    public Subscription(String title, String token) {
        this.title = title;
        this.token = token;
    }

    public Subscription(){

    }
}
