package com.kurata.hotelmanagement.dataSource.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthSource {
    private static final String TAG = "FirebaseAuthSource";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public FirebaseAuthSource(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
    }

    //get current user uid
    public String getCurrentUid() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    //get current user
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

}
