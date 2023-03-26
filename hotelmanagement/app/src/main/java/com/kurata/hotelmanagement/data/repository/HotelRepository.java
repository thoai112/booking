package com.kurata.hotelmanagement.data.repository;

import android.content.Context;
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
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;

import java.util.ArrayList;
import java.util.List;

public class HotelRepository {

        private static final String TAG = "HOTELTYPE REPO";
        private Preference preferenceManager;
        private Hoteltype hoteltype = new Hoteltype();
        private Hotel hotel = new Hotel();
        private static HotelRepository instance;
        private ArrayList<Hoteltype> arraylist =new ArrayList<>();
        private Context context;
        final  private FirebaseStorage storage = FirebaseStorage.getInstance();
        final  private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


        public static HotelRepository getInstance(){
                if (instance==null){
                        instance= new HotelRepository();
                }
                return instance;

        }


        //TODO - GETALL HOTELTYPE
        public MutableLiveData<List<Hoteltype>> getAllHoteltype() {
                MutableLiveData<List<Hoteltype>> allHoteltype = new MutableLiveData<>();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("rating").addSnapshotListener((value, error) -> {
                        if (error != null) return;
                        if (value != null) {
                                List<Hoteltype> tempList = new ArrayList<>();
                                for (DocumentSnapshot document : value.getDocuments()) {
                                        Hoteltype model = document.toObject(Hoteltype.class);
                                        assert model != null;
                                                tempList.add(model);
                                }
                                allHoteltype.postValue(tempList);
                        }
                });
                return allHoteltype;

        }


        //TODO - GETALL HOTELS
        public MutableLiveData<List<Hotel>> getAllHotels(String uid) {
                MutableLiveData<List<Hotel>> allHotel = new MutableLiveData<>();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("rating").document(uid).collection("hotels").addSnapshotListener((value, error) -> {
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


        //update displayImage
        public void uploadImage(final Uri imageProfile, String document, String uid) {
                final StorageReference reference = storage.getReference()
                        .child("categoryhotel_picture")
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


}
