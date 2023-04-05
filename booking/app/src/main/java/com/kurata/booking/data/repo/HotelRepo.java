package com.kurata.booking.data.repo;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.utils.Preference;

import java.util.ArrayList;
import java.util.List;

public class HotelRepo {
    private static final String TAG = "HotelRepo";
    private Preference preferenceManager;
    private Hotel hotel = new Hotel();
    private static HotelRepo instance;

    public static HotelRepo getInstance(){
        if (instance==null){
            instance= new HotelRepo();
        }
        return instance;
    }

    //get ID hotel popular
    public MutableLiveData<List<String>> getPoproom() {
        MutableLiveData<List<String>> allpop= new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        //ArrayList<String> test = new ArrayList<>();
        CollectionReference applicationsRef = firestore.collection("popular");
        DocumentReference applicationIdRef = applicationsRef.document("pop");
        applicationIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> tempList = new ArrayList<String>();
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    tempList = (List<String>) document.get("hotel_id");
                }
                allpop.postValue(tempList);
            }
        });
        return allpop;
    }
    //get all hotel popular
//    public MutableLiveData<List<Hotel>> getHotelPopular() {
//        MutableLiveData<List<Hotel>> allHotel = new MutableLiveData<>();
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//        //todo - get hotel_id popular
//        CollectionReference applicationsRef = firestore.collection("popular");
//        DocumentReference applicationIdRef = applicationsRef.document("pop");
//        applicationIdRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    //todo - get all hotel popular with id
//                    firestore.collectionGroup("hotels").whereEqualTo("id", document.get("room_id")).addSnapshotListener((value, error) -> {
//                        if (error != null) return;
//                        if (value != null) {
//                            List<Hotel> tempList = new ArrayList<>();
//                            for (DocumentSnapshot document1 : value.getDocuments()) {
//                                Hotel model = document1.toObject(Hotel.class);
//                                assert model != null;
//                                tempList.add(model);
//                                Log.d("SDGFHD", model.toString());
//                            }
//                            allHotel.postValue(tempList);
//                        }
//                    });
//                }
//            }
//        });
//        Log.d("all hotel", allHotel.toString());
//        return allHotel;
//    }

    //get value hotel activate
    public MutableLiveData<List<Hotel>> getHotelActivate() {
        MutableLiveData<List<Hotel>> allHotel = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collectionGroup("hotels").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Hotel> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Hotel model = document.toObject(Hotel.class);
                    assert model != null;
                    tempList.add(model);
                }
                allHotel.postValue(tempList);
            }
        });
        return allHotel;
    }



}
