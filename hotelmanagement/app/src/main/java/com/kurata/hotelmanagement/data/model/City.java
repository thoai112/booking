package com.kurata.hotelmanagement.data.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class City implements Serializable {
    private String id;
    private String name;
    private GeoPoint location;
    private String image;

    public City() {
    }

    public City(String id, String name, GeoPoint location, String image) {
        this.id = id;
        this.name = name;
        this.location = location;
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

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }

}

