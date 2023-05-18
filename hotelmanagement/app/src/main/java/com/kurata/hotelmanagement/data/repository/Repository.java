package com.kurata.hotelmanagement.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kurata.hotelmanagement.data.model.Popula;
import com.kurata.hotelmanagement.data.model.Promotion;
import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.utils.Preference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Repository {

    private static final String TAG = "Repo";
    private Preference preferenceManager;
    private static Repository instance;
    private  User user = new User();
    private ArrayList<User> arraylist =new ArrayList<>();
    private ArrayList<Popula> popularlist = new ArrayList<>();
    private ArrayList<Promotion> promotions = new ArrayList<>();


    public static Repository getInstance(){
        if (instance==null){
            instance= new Repository();
        }
        return instance;

    }

    public MutableLiveData<User> getData(String uid){
        final MutableLiveData<User> data = new MutableLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("users").document(uid);

        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                  user = documentSnapshot.toObject(User.class);

                  data.setValue(user);
            }
        });
        return data;
    }


    //update displayImage
    public void updateDisplayImage(final Uri imageProfile, FirebaseFirestore firestore, String uid, FirebaseStorage storage) {
        final StorageReference reference = storage.getReference()
                .child("profile_picture")
                .child(FirebaseAuth.getInstance().getUid());

        final DocumentReference db_reference = firestore.collection("users").document(uid);

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
                            db_reference.update("avatar", task.getResult().toString())
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


    //todo - get user
    // get all user
    public MutableLiveData<List<User>> getAllUser(String myId) {
        MutableLiveData<List<User>> allUser = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<User> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    User model = document.toObject(User.class);
                    assert model != null;
                    if (!model.getId().equals(myId)) {
                        tempList.add(model);
                    }
                }
                allUser.postValue(tempList);
            }
        });
        return allUser;
    }
    //todo - get user with role
    public MutableLiveData<List<User>> getUser(String myId, String role) {
        MutableLiveData<List<User>> allUser = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("role",role).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<User> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    User model = document.toObject(User.class);
                    assert model != null;
                    if (!model.getId().equals(myId)) {
                        tempList.add(model);
                    }
                }
                allUser.postValue(tempList);
            }
        });
        return allUser;
    }


    //todo - get pop

    public MutableLiveData<List<String>> getPopz() {
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
                    tempList = (List<String>) document.get("room_id");
                }
                allpop.postValue(tempList);
            }
        });
        return allpop;
    }

    //todo - get promotion
    public MutableLiveData<List<Promotion>> getAllPromotion() {
        MutableLiveData<List<Promotion>> allPromotion = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("promotions").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Promotion> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Promotion model = document.toObject(Promotion.class);
                    assert model != null;
                    tempList.add(model);
                    Log.d("name", model.getName());
                }
                allPromotion.postValue(tempList);
            }
        });
        return allPromotion;
    }

//    public MutableLiveData<List<Promotion>> getAllPromotion() {
//        MutableLiveData<List<Promotion>> allPromotion = new MutableLiveData<>();
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        firestore.collection("promotions").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    Log.w(TAG, "Listen failed.", error);
//                    return;
//                }
//                List<Promotion> promotions = new ArrayList<>();
//
//                for (QueryDocumentSnapshot document : value) {
//                    Promotion promotion = document.toObject(Promotion.class);
//                    promotions.add(promotion);
//                }
//
//                allPromotion.setValue(promotions);
//            }
//        });
//        return allPromotion;
//    }


}