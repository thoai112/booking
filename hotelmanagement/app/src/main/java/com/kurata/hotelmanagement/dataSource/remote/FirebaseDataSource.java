package com.kurata.hotelmanagement.dataSource.remote;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.StorageReference;
import com.kurata.hotelmanagement.utils.Constants;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;

public class FirebaseDataSource {

    private static final String TAG = "FirebaseDataSource";

    private FirebaseAuthSource firebaseAuthSource;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String currentUid;

    @Inject
    public FirebaseDataSource(FirebaseAuthSource firebaseAuthSource, FirebaseFirestore firebaseFirestore, StorageReference storageReference) {
        this.firebaseAuthSource = firebaseAuthSource;
        this.firebaseFirestore = firebaseFirestore;
        this.storageReference = storageReference;
        currentUid = firebaseAuthSource.getCurrentUid();
    }

    public Flowable<DocumentSnapshot> getUserInfo(final String uid) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                DocumentReference reference = firebaseFirestore.collection("users").document(uid);
                final ListenerRegistration registration = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            emitter.onError(e);
                        }
                        if (documentSnapshot != null) {
                            emitter.onNext(documentSnapshot);
                        }
                    }
                });

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
    }


}
