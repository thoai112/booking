package com.kurata.hotelmanagement.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kurata.hotelmanagement.data.model.Room;
import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository {
    private static final String TAG = "ROOMTYPE REPO";
    private Preference preferenceManager;
    private Roomtype roomtype = new Roomtype();
    private static RoomRepository instance;
    private ArrayList<Roomtype> arraylist =new ArrayList<>();
    final  private FirebaseStorage storage = FirebaseStorage.getInstance();
    final  private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    public static RoomRepository getInstance(){
        if (instance==null){
            instance= new RoomRepository();
        }
        return instance;

    }


    //TODO - GETALL ROOMTYPE
    public MutableLiveData<List<Roomtype>> getAllRoomtype() {
        MutableLiveData<List<Roomtype>> allRoomtype = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("rooms").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Roomtype> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Roomtype model = document.toObject(Roomtype.class);
                    assert model != null;
                    tempList.add(model);
                }
                allRoomtype.postValue(tempList);
            }
        });
        return allRoomtype;

    }


    //TODO - GETALL ROOMS - Filter
    public MutableLiveData<List<Room>> getAllRooms(String hoteltype_id, String hotel_id, String roomtype_id) {
        MutableLiveData<List<Room>> allRoom = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        //String hoteltype_id, String hotel_id, String roomtype_id
        //whereEqualTo("hoteltype_id", hoteltype_id).whereEqualTo("hotel_id", hotel_id).whereEqualTo("roomtype_id", roomtype_id)
        firestore.collectionGroup("room").whereEqualTo("hoteltype_id", hoteltype_id).whereEqualTo("hotel_id", hotel_id).whereEqualTo("roomtype_id", roomtype_id).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Room> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Room model = document.toObject(Room.class);
                    assert model != null;
                    tempList.add(model);
                }
                allRoom.postValue(tempList);
            }
        });
        return allRoom;
    }

    //get all room
    public MutableLiveData<List<Room>> getAllRoom() {
        MutableLiveData<List<Room>> allRoom = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        //String hoteltype_id, String hotel_id, String roomtype_id
        //whereEqualTo("hoteltype_id", hoteltype_id).whereEqualTo("hotel_id", hotel_id).whereEqualTo("roomtype_id", roomtype_id)
        firestore.collection("room").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Room> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Room model = document.toObject(Room.class);
                    assert model != null;
                    tempList.add(model);
                }
                allRoom.postValue(tempList);
            }
        });
        return allRoom;
    }

    //TODO - update displayImage
    public void uploadImage(final Uri imageProfile, String document, String uid) {
        final StorageReference reference = storage.getReference()
                .child("categoryroom_picture")
                .child(uid);

        final DocumentReference db_reference = firestore.collection(document).document(uid);

        //UploadTask uploadTask = reference.putBytes(DataConverter.convertImage2ByteArray(bitmap));
        UploadTask uploadTask = reference.putFile(imageProfile);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("err", e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //get image download url;
                reference.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("err", e.toString());
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(!task.isSuccessful()){
                            Log.d("upload","failed");
                        }
                        else{
                            db_reference.update(Constants.H_IMG, task.getResult().toString())
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("error",e.toString());
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    //get value roomtype Activate
    public MutableLiveData<List<Roomtype>> getRoomtypeActivate() {
        MutableLiveData<List<Roomtype>> allRoomtype = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("rooms").whereEqualTo("status",Boolean.TRUE).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Roomtype> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Roomtype model = document.toObject(Roomtype.class);
                    assert model != null;
                    tempList.add(model);
                }
                allRoomtype.postValue(tempList);
            }
        });
        return allRoomtype;

    }

    //TODO - get value rooms Activate


}
