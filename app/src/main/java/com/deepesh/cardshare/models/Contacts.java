package com.deepesh.cardshare.models;

public class Contacts {
    private int id;
    private String phNumber;
    private String name;

    public Contacts(int id, String name, String phNumber) {
        this.id = id;
        this.phNumber = phNumber;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


