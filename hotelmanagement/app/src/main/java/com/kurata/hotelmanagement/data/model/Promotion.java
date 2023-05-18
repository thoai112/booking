package com.kurata.hotelmanagement.data.model;
//


import com.google.firebase.Timestamp;

import java.io.Serializable;



//
public class Promotion implements Serializable {
    String id;
    Timestamp time_start;
    Timestamp time_end;
    Timestamp created_at;
    String name;
    String code;
    String description;
    String hotelID;
    String hoteltypeID;
    String roomtypeID;
    String roomID;
    String userID;
    String img;
    String sum;
    String remai;
    String discount_ratio;

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public long getRemainDay(){
//        return ChronoUnit.DAYS.between(time_end.toLocalDateTime(), LocalDateTime.now());
//    }
    public Promotion() {
    }

    public Promotion(String id, String code, Timestamp time_start, Timestamp time_end, Timestamp created_at, String name, String description, String hoteltypeID, String hotelID, String roomtypeID, String roomID, String userID, String img, String sum, String remai, String discount_ratio) {

        this.id = id;
        this.time_start = time_start;
        this.time_end = time_end;
        this.created_at = created_at;
        this.name = name;
        this.code = code;
        this.description = description;
        this.hotelID = hotelID;
        this.hoteltypeID = hoteltypeID;
        this.roomtypeID = roomtypeID;
        this.roomID = roomID;
        this.userID = userID;
        this.img = img;
        this.sum = sum;
        this.remai = remai;
        this.discount_ratio = discount_ratio;
    }

    public Timestamp getTime_start() {
        return time_start;
    }

    public void setTime_start(Timestamp time_start) {
        this.time_start = time_start;
    }

    public Timestamp getTime_end() {
        return time_end;
    }

    public void setTime_end(Timestamp time_end) {
        this.time_end = time_end;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHotelID() {
        return hotelID;
    }

    public void setHotelID(String hotelID) {
        this.hotelID = hotelID;
    }

    public String getHoteltypeID() {
        return hoteltypeID;
    }

    public void setHoteltypeID(String hoteltypeID) {
        this.hoteltypeID = hoteltypeID;
    }

    public String getRoomtypeID() {
        return roomtypeID;
    }

    public void setRoomtypeID(String roomtypeID) {
        this.roomtypeID = roomtypeID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getDiscount_ratio() {
        return discount_ratio;
    }

    public void setDiscount_ratio(String discount_ratio) {
        this.discount_ratio = discount_ratio;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


//public class Promotion implements Parcelable {
//    String id;
//    Timestamp time_start;
//    Timestamp time_end;
//    Timestamp created_at;
//    String name;
//    String code;
//    String description;
//    String hotelID;
//    String hoteltypeID;
//    String roomtypeID;
//    String roomID;
//    String userID;
//    String img;
//    String sum;
//    String remai;
//    String discount_ratio;
//
//    public Promotion() {
//        // no-argument constructor
//    }
//
//    public Promotion(String id, String code, Timestamp time_start, Timestamp time_end, Timestamp created_at, String name, String description, String hoteltypeID, String hotelID, String roomtypeID, String roomID, String userID, String img, String sum, String remai, String discount_ratio) {
//        this.id = id;
//        this.time_start = time_start;
//        this.time_end = time_end;
//        this.created_at = created_at;
//        this.name = name;
//        this.code = code;
//        this.description = description;
//        this.hotelID = hotelID;
//        this.hoteltypeID = hoteltypeID;
//        this.roomtypeID = roomtypeID;
//        this.roomID = roomID;
//        this.userID = userID;
//        this.img = img;
//        this.sum = sum;
//        this.remai = remai;
//        this.discount_ratio = discount_ratio;
//    }
//
//    protected Promotion(Parcel in) {
//        id  = in.readString();
//        name = in.readString();
//        code = in.readString();
//        description = in.readString();
//        hotelID = in.readString();
//        hoteltypeID = in.readString();
//        roomtypeID = in.readString();
//        roomID = in.readString();
//        userID = in.readString();
//        img = in.readString();
//        sum = in.readString();
//        remai = in.readString();
//        discount_ratio = in.readString();
//        time_start = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
//        time_end = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
//        created_at = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
//    }
//
//    public static final Creator<Promotion> CREATOR = new Creator<Promotion>() {
//        @Override
//        public Promotion createFromParcel(Parcel in) {
//            return new Promotion(in);
//        }
//
//        @Override
//        public Promotion[] newArray(int size) {
//            return new Promotion[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
//        dest.writeString(name);
//        dest.writeString(code);
//        dest.writeString(description);
//        dest.writeString(hotelID);
//        dest.writeString(hoteltypeID);
//        dest.writeString(roomtypeID);
//        dest.writeString(roomID);
//        dest.writeString(sum);
//        dest.writeString(remai);
//        dest.writeString(img);
//        dest.writeValue(time_start);
//        dest.writeValue(time_end);
//        dest.writeValue(created_at);
//
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public Timestamp getTime_start() {
//        return time_start;
//    }
//
//    public Timestamp getTime_end() {
//        return time_end;
//    }
//
//    public Timestamp getCreated_at() {
//        return created_at;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public String getHotelID() {
//        return hotelID;
//    }
//
//    public String getHoteltypeID() {
//        return hoteltypeID;
//    }
//
//    public String getRoomtypeID() {
//        return roomtypeID;
//    }
//
//    public String getRoomID() {
//        return roomID;
//    }
//
//    public String getUserID() {
//        return userID;
//    }
//
//    public String getImg() {
//        return img;
//    }
//
//    public String getSum() {
//        return sum;
//    }
//
//    public String getRemai() {
//        return remai;
//    }
//
//    public String getDiscount_ratio() {
//        return discount_ratio;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
}

