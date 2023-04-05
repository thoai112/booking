package com.kurata.booking.data.repo;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kurata.booking.data.model.User;
import com.kurata.booking.utils.Preference;

public class AccountRepo {

    private static final String TAG = "AccountRepo";
    private Preference preferenceManager;
    private  User user = new User();
    private static AccountRepo instance;

    public static AccountRepo getInstance(){
        if (instance==null){
            instance= new AccountRepo();
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

    //todo - upload image
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





}
