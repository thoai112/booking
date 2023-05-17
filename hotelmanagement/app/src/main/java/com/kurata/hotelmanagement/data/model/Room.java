package com.kurata.hotelmanagement.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable{
    private String id;
    private String name;
    private String price;
    private String about;
    private String hoteltype_id;
    private String hotel_id;
    private String roomtype_id;
    private ArrayList<String> image;
    private String sum;
    private String remai;
    private Boolean status;

    public Room(){

    }

    public Room(String id, String name, String price, String about, String hoteltype_id, String hotel_id, String roomtype_id, ArrayList<String> image, Boolean status, String sum, String remai) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.about = about;
        this.hoteltype_id = hoteltype_id;
        this.hotel_id = hotel_id;
        this.roomtype_id=roomtype_id;
        this.image = image;
        this.status = status;
        this.sum = sum;
        this.remai = remai;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getHoteltype_id() {
        return hoteltype_id;
    }

    public void setHoteltype_id(String hoteltype_id) {
        this.hoteltype_id = hoteltype_id;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getRoomtype_id() {
        return roomtype_id;
    }

    public void setRoomtype_id(String roomtype_id) {
        this.roomtype_id = roomtype_id;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getRemai() {
        return remai;
    }

    public void setRemai(String remai) {
        this.remai = remai;
    }

    @Override
    public String toString() {
        return name;
    }
}
