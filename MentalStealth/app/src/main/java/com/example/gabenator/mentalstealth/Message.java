package com.example.gabenator.mentalstealth;

/**
 * Created by Gabenator on 10/28/2017.
 */

public class Message {
    private int type;
    private String body;
    private long date_Sent;
    private String from;
    private double rating;

    public Message(int t, String b, long d, String f) {
        this.type = t;
        this.body = b;
        this.date_Sent = d;
        this.from = f;
    }

    public Message(int t, String b, long d, String f, double r) {
        this.type = t;
        this.body = b;
        this.date_Sent = d;
        this.from = f;
        this.rating = r;
    }

    public int getType() {
        return this.type;
    }

    public String getContent() {
        return this.body;
    }

    public long getDate_Sent() {
        return this.date_Sent;
    }

    public String getNumber() {
        return this.from;
    }

    public double getRating() {
        return this.rating;
    }

    public void setRating(double d) {
        this.rating = d;
    }
}
