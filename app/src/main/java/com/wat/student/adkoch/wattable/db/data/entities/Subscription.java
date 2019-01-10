package com.wat.student.adkoch.wattable.db.data.entities;

public final class Subscription {
    private String name;
    private String token;


    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }



    public Subscription(String name, String token) {
        this.name = name;
        this.token = token;
    }
}
