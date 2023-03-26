package com.kurata.hotelmanagement.ui.user;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.hotelmanagement.adapter.UsersRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.databinding.FragmentUsersBinding;
import com.kurata.hotelmanagement.databinding.PopupUserProfileBinding;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

public class Users extends Fragment implements UsersRecyclerAdapter.UserListener {

    private static final String TAG = "UsersFragment_Tag";
    ArrayList<User> list = new ArrayList<User>();

    private FirebaseAuth mAuth;
    private UsersViewModel usersViewModel;
    private FragmentUsersBinding binding;
    private PopupUserProfileBinding UBinding;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Inject
    UsersRecyclerAdapter recyclerAdapter;
    @Inject
    RequestManager requestManager;

    public Users() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        String myId = mAuth.getCurrentUser().getUid();
        Log.d("uidtest", myId);

        usersViewModel =  new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        usersViewModel.init(myId);

        recyclerAdapter = new UsersRecyclerAdapter(list, this);
        binding.RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.RecyclerView.setAdapter(recyclerAdapter);

        usersViewModel.getAllUsersData().observe(getViewLifecycleOwner(), userModels -> {
            list.clear();
            list.addAll(userModels);
            recyclerAdapter.notifyDataSetChanged();
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupAddUserProfile(Gravity.TOP);
            }
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

        return view;

    }

//    private void setListener(){
//        binding.back.setOnClickListener(v -> onBackPressed());
//    }

//    private void loading(Boolean isLoading){
//        if(isLoading){
//            binding.progressBar.setVisibility(View.VISIBLE);
//        }
//        else {
//            binding.progressBar.setVisibility(View.INVISIBLE);
//        }
//    }

    private void filter(String text) {

        ArrayList<User> filteredList = new ArrayList<User>();
        for (User item: list){
            Log.e("item", "item is: " +item.getfullName() +"  \n\n");
            if (item.getfullName().toLowerCase().contains(text.toLowerCase())){
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

    private void DeleteUserProfile(FirebaseFirestore firestore, String uid){
//        FirebaseAuth.getInstance().getUid().delete();
        firestore.collection("users").document(uid)
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

    private void PopupAddUserProfile(int gravity) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UBinding = PopupUserProfileBinding.inflate(getLayoutInflater());
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

        UBinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


    private void PopupUserProfile(int gravity, FirebaseFirestore firestore, String uid) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UBinding = PopupUserProfileBinding.inflate(getLayoutInflater());
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


        UBinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> user = new HashMap<>();
                user.put("fullName",UBinding.txtfullName.getText().toString());
                user.put("email",UBinding.txtEmail.getText().toString());
                user.put("address",UBinding.txtaddress.getText().toString());
                user.put("mobile",UBinding.txtmobile.getText().toString());
                user.put("role",UBinding.txtrole.getText().toString());
                user.put("zip",UBinding.txtzip.getText().toString());

                DocumentReference reference = firestore.collection("users").document(uid);

                reference.update(user).addOnCompleteListener(task -> {
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
                DeleteUserProfile(firestore, uid);
                dialog.dismiss();
            }

        });

        dialog.show();

    }

    @Override
    public void onUserClicked(User user) {
        PopupUserProfile(Gravity.TOP ,firestore, user.getId());
        Log.d("UID", user.getId());
        UBinding.txtfullName.setText(user.getfullName());
        UBinding.txtEmail.setText(user.getEmail());
        UBinding.txtaddress.setText(user.getAddress());
        UBinding.txtmobile.setText(user.getMobile());
        UBinding.txtrole.setText(user.getRole());
        UBinding.txtzip.setText(user.getZip());
        Glide.with(UBinding.Logo).load(user.getAvatar()).into(UBinding.Logo);
    }
}