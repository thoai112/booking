package com.kurata.hotelmanagement.ui.roomtype;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.hotelmanagement.adapter.RoomtypesRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.data.repository.HotelRepository;
import com.kurata.hotelmanagement.data.repository.RoomRepository;
import com.kurata.hotelmanagement.databinding.FragmentRoomTypeBinding;
import com.kurata.hotelmanagement.databinding.PopupHoteltypeBinding;
import com.kurata.hotelmanagement.utils.Preference;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

public class RoomType extends Fragment implements RoomtypesRecyclerAdapter.RoomtypeListener{

    private static final String TAG = "RoomtypesFragment_Tag";
    ArrayList<Roomtype> list = new ArrayList<Roomtype>();
    private static final int REQUEST_CODE = 1;
    private Preference preferenceManager;
    //private FirebaseAuth mAuth;
    private RoomTypeViewModel mViewModel;
    private FragmentRoomTypeBinding binding;
    private PopupHoteltypeBinding UBinding;
    private Uri selectedImage;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Inject
    RoomtypesRecyclerAdapter recyclerAdapter;



    public RoomType () {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentRoomTypeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mViewModel =  new ViewModelProvider(requireActivity()).get(RoomTypeViewModel.class);
        mViewModel.init();

        recyclerAdapter = new RoomtypesRecyclerAdapter(list, this);
        binding.RecyclerView.setHasFixedSize(true);
        binding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.RecyclerView.setAdapter(recyclerAdapter);

        mViewModel.getAllHoteltypeData().observe(getViewLifecycleOwner(), roomModels -> {
            list.clear();
            list.addAll(roomModels);
            recyclerAdapter.notifyDataSetChanged();
        });


        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }

        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupAddRoomtype(Gravity.CENTER);
            }
        });

        return view;
    }


    private void filter(String text) {

        ArrayList<Roomtype> filteredList = new ArrayList<Roomtype>();
        for (Roomtype item: list){
            Log.e("item", "item is: " +item.getName() +"  \n\n");
            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()){
            filteredList.clear();
            recyclerAdapter.setFilteredList(filteredList);
        }
        else {
            recyclerAdapter.setFilteredList(filteredList);
        }


    }

    private void PopupAddRoomtype(int gravity) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UBinding = PopupHoteltypeBinding.inflate(getLayoutInflater());
        dialog.setContentView(UBinding.getRoot());

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);


        UBinding.mDelete.setVisibility(View.INVISIBLE);
        UBinding.mUpdate.setText("Thêm");

        UBinding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGallery();
            }
        });

        UBinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("name",UBinding.txttitle.getText().toString());
                data.put("type",UBinding.txtrate.getText().toString());
                DocumentReference addedDocRef = firestore.collection("rating").document();
                data.put("id",addedDocRef.getId().toString());
                data.put("img","");
                firestore.collection("rating").document(addedDocRef.getId()).set(data);

                if(selectedImage!=null){
                    HotelRepository repository = new HotelRepository();
                    repository.uploadImage(selectedImage,"rating",addedDocRef.getId());
                }
                dialog.dismiss();

            }
        });


        UBinding.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void DeleteRoomtype(FirebaseFirestore firestore, String uid){
        firestore.collection("rooms").document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(getContext(), "Delete successfully deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(getContext(), "Error deleting document.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RoomTypeViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showGallery() {
        //Storage permission
        RealRxPermission.getInstance(getContext())
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe();
        //Open gallery Intent
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto , REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if (resultCode==RESULT_OK){
                selectedImage = data.getData();
                UBinding.image.setImageURI(selectedImage);
            }
        }
    }

    private void PopupRoomtype(int gravity, String uid) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UBinding = PopupHoteltypeBinding.inflate(getLayoutInflater());
        dialog.setContentView(UBinding.getRoot());

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);


        UBinding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGallery();
            }
        });

        UBinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> roomtype = new HashMap<>();
                roomtype.put("name",UBinding.txttitle.getText().toString());
                //hoteltype.put("",UBinding.txtrate.getText().toString());

                if(selectedImage!=null){
                    RoomRepository repository = new RoomRepository();
                    repository.uploadImage(selectedImage,"rooms",uid);
                }
                DocumentReference reference = firestore.collection("rooms").document(uid);

                reference.update(roomtype).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Update Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    else{
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        UBinding.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        UBinding.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteRoomtype(firestore,uid);
                dialog.dismiss();
            }

        });

        dialog.show();

    }

    @Override
    public void onUserClicked(Roomtype roomtype) {
        PopupRoomtype(Gravity.CENTER, roomtype.getId());
        UBinding.txttitle.setText(roomtype.getName());
        Glide.with(UBinding.image).load(roomtype.getImg()).into(UBinding.image);
    }
}