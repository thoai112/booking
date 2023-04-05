package com.kurata.booking.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Hotel implements Serializable {
    private String id;
    private String name;
    private String address;
    private String about;
    private String status;
    private ArrayList<String> image;

    public Hotel() {

    }

    public Hotel(String id, String name, String address, String about, String status, ArrayList<String> image) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.about = about;
        this.status = status;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }
}
