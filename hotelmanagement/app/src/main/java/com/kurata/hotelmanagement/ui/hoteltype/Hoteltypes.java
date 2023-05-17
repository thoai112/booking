package com.kurata.hotelmanagement.ui.hoteltype;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.HoteltypesRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.data.repository.HotelRepository;
import com.kurata.hotelmanagement.databinding.FragmentHoteltypesBinding;
import com.kurata.hotelmanagement.databinding.PopupHoteltypeBinding;
import com.kurata.hotelmanagement.utils.Preference;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

public class Hoteltypes extends Fragment implements HoteltypesRecyclerAdapter.HoteltypeListener {

    private static final String TAG = "HoteltypesFragment_Tag";
    ArrayList<Hoteltype> list = new ArrayList<Hoteltype>();
    private static final int REQUEST_CODE = 1;
    private Preference preferenceManager;

    //private FirebaseAuth mAuth;
    private HoteltypesViewModel mViewModel;
    private FragmentHoteltypesBinding binding;
    private PopupHoteltypeBinding UBinding;
    private Uri selectedImage;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //Spiner
    ArrayAdapter<String> adapterItems;
    private String[] items =  {"Activate","Disable"};
    String item;

    @Inject
    HoteltypesRecyclerAdapter recyclerAdapter;


    public Hoteltypes() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHoteltypesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mViewModel =  new ViewModelProvider(requireActivity()).get(HoteltypesViewModel.class);
        mViewModel.init();

        recyclerAdapter = new HoteltypesRecyclerAdapter(list, this);
        binding.RecyclerView.setHasFixedSize(true);
        binding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.RecyclerView.setAdapter(recyclerAdapter);

        mViewModel.getAllHoteltypeData().observe(getViewLifecycleOwner(), hotelModels -> {
            list.clear();
            list.addAll(hotelModels);
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
                PopupAddHoteltype(Gravity.CENTER);
            }
        });

        binding.back.setOnClickListener(v-> getActivity().onBackPressed());


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HoteltypesViewModel.class);
        // TODO: Use the ViewModel
    }


    private void filter(String text) {

        ArrayList<Hoteltype> filteredList = new ArrayList<Hoteltype>();
        for (Hoteltype item: list){
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

    private void PopupAddHoteltype(int gravity) {
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

        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.drop_down_item, items);
        UBinding.autoCompleteTxt.setAdapter(adapterItems);

        UBinding.autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
            }
        });

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

                if(item ==(items[0])){
                    data.put("status",Boolean.TRUE);
                }else{
                    data.put("Status", Boolean.FALSE);
                }

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


    private void DeleteHoteltype(FirebaseFirestore firestore, String uid){
        firestore.collection("rating").document(uid)
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

    private void PopupHotelype(int gravity, String uid, Boolean status) {
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

        if(status){
            UBinding.autoCompleteTxt.setText(items[0]);
        }
        else{
            UBinding.autoCompleteTxt.setText(items[1]);
        }

        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.drop_down_item, items);
        UBinding.autoCompleteTxt.setAdapter(adapterItems);

        UBinding.autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
            }
        });

        UBinding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGallery();
            }
        });

        UBinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hoteltype = new HashMap<>();
                hoteltype.put("name",UBinding.txttitle.getText().toString());
                hoteltype.put("type",UBinding.txtrate.getText().toString());
                if(UBinding.autoCompleteTxt.getText().toString().equals(items[0])){
                    hoteltype.put("status",Boolean.TRUE);
                }else{
                    hoteltype.put("status",Boolean.FALSE);
                }

                if(selectedImage!=null){
                    HotelRepository repository = new HotelRepository();
                    repository.uploadImage(selectedImage,"rating",uid);
                }
                DocumentReference reference = firestore.collection("rating").document(uid);

                reference.update(hoteltype).addOnCompleteListener(task -> {
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
                DeleteHoteltype(firestore,uid);
                dialog.dismiss();
            }

        });

        dialog.show();

    }


    @Override
    public void onUserClicked(Hoteltype hoteltype) {
        PopupHotelype(Gravity.CENTER, hoteltype.getId(), hoteltype.getStatus());
        UBinding.txttitle.setText(hoteltype.getName());
        UBinding.txtrate.setText(hoteltype.getType());
        Glide.with(UBinding.image).load(hoteltype.getImg()).into(UBinding.image);
    }
}