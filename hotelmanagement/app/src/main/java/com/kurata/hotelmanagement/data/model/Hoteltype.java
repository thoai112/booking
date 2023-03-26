package com.kurata.hotelmanagement.data.model;

public class Hoteltype {
    String id;
    String name;
    String type;
    String img;

    public Hoteltype(){

    }

    public Hoteltype(String id, String name, String type, String img) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.img = img;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


}
