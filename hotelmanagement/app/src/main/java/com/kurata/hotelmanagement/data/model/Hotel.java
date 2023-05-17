package com.kurata.hotelmanagement.data.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Hotel implements Serializable {
    private String id;
    private String name;
    private String address;
    private String about;
    private Boolean status;
    private GeoPoint location;
    private String userID;
    private String citiID;
    private String hoteltypeID;
    private ArrayList<String> image;

    public Hotel() {

    }


    public Hotel(String id, String name, String address, String about, boolean status, GeoPoint location, String userID, String citiID, String hoteltypeID, ArrayList<String> image) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.about = about;
        this.status = status;
        this.location = location;
        this.userID = userID;
        this.citiID = citiID;
        this.hoteltypeID = hoteltypeID;
        this.image = image;
    }

//    protected Hotel(Parcel in) {
//        id = in.readString();
//        name = in.readString();
//        address = in.readString();
//        about = in.readString();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            status = in.readBoolean();
//        }
//        userID = in.readString();
//        image = in.createStringArrayList();
//    }



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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCitiID() {
        return citiID;
    }

    public void setCitiID(String citiID) {
        this.citiID = citiID;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getHoteltypeID() {
        return hoteltypeID;
    }

    public void setHoteltypeID(String hoteltypeID) {
        this.hoteltypeID = hoteltypeID;
    }

    @Override
    public String toString() {
        return name;
    }

}
