package com.kurata.hotelmanagement.data.model;

public class Roomtype {
    String id;
    String name;
    String img;
    Boolean status;

    public Roomtype(){

    }

    public Roomtype(String id, String name, String img, Boolean status) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.status = status;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name;
    }
}
