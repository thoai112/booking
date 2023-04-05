package com.kurata.booking.ui.profile;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.kurata.booking.data.model.User;
import com.kurata.booking.data.repo.AccountRepo;
import com.kurata.booking.databinding.FragmentProfileBinding;
import com.kurata.booking.databinding.PopupAccountBinding;
import com.kurata.booking.databinding.PopupChangePasswordBinding;
import com.kurata.booking.ui.main.MainActivity;
import com.kurata.booking.utils.Constants;
import com.kurata.booking.utils.Preference;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.HashMap;

public class Profile extends Fragment {

    private static final int REQUEST_CODE = 1;
    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;
    private Preference preferenceManager;
    private String currentId;
    private FirebaseFirestore firestore;
    private PopupAccountBinding Ebinding;
    private PopupChangePasswordBinding Cbinding;
    FirebaseStorage storage;

    public Profile() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        View view = binding.getRoot();



        preferenceManager = new Preference(getContext());


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentId = firebaseUser.getUid();
        DocumentReference reference;
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        ViewModelProvider.Factory test = (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory();

        mViewModel = new ViewModelProvider(getActivity(), test).get(ProfileViewModel.class);
        //profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
        mViewModel.init(currentId);
        mViewModel.getAccount().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                UpdateProfileViews(user);
            }
        });


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));

            }
        });

        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupEditProfile(Gravity.CENTER, firestore,currentId, firebaseUser);
            }
        });

        binding.Logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGallery();
            }
        });

    return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }


    //todo - update image
    private void showGallery() {
        //Storage permission
        RealRxPermission.getInstance(getContext())
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe();
        //Open gallery Intent
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto , REQUEST_CODE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if (resultCode==RESULT_OK){
                Uri selectedImage = data.getData();
                preferenceManager.putString(Constants.P_AVATAR,selectedImage.toString());
                Log.d("avatar", Constants.P_AVATAR);
                if(selectedImage!=null){
                    AccountRepo repository = new AccountRepo();
                    repository.updateDisplayImage(selectedImage,firestore,currentId,storage);
                }
            }
        }
    }

    private void UpdateProfileViews(User user ) {
        binding.fullName.setText(user.getfullName());
        binding.email.setText(user.getEmail());
        Glide.with(this).load(user.getAvatar()).into(binding.Logo);

        preferenceManager.putString(Constants.P_FULL_NAME, user.getfullName() );
        preferenceManager.putString(Constants.P_EMAIL, user.getEmail() );
        preferenceManager.putString(Constants.P_ADDRESS, user.getAddress());
        preferenceManager.putString(Constants.P_MOBILE, user.getMobile() );
        preferenceManager.putString(Constants.P_ROLE, user.getRole() );
        preferenceManager.putBoolean(String.valueOf(Constants.P_STATUS), user.isStatus());
        preferenceManager.putString(Constants.P_AVATAR, user.getAvatar());
    }

    private void PopupEditProfile(int gravity, FirebaseFirestore firebaseFirestore, String uid, FirebaseUser firebaseUser) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Ebinding = PopupAccountBinding.inflate(getLayoutInflater());
        dialog.setContentView(Ebinding.getRoot());

        ViewEditProfile();

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

        Ebinding.mchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                CreatepopUpwindowChangePass(Gravity.CENTER,firebaseUser);
            }
        });

        Ebinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkValidInputEProfile()){
                    Toast.makeText(getActivity(), "field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> user = new HashMap<>();
                user.put("fullName",Ebinding.txtfullName.getText().toString());
                user.put("email",Ebinding.txtEmail.getText().toString());
                user.put("address",Ebinding.txtAddress.getText().toString());
                user.put("mobile",Ebinding.txtMobile.getText().toString());


                DocumentReference reference = firebaseFirestore.collection("users").document(uid);

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

        Ebinding.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void ViewEditProfile() {
        Ebinding.txtfullName.setText(preferenceManager.getString(Constants.P_FULL_NAME));
        Ebinding.txtEmail.setText(preferenceManager.getString(Constants.P_EMAIL));
        Ebinding.txtMobile.setText(preferenceManager.getString(Constants.P_MOBILE));
        Ebinding.txtAddress.setText(preferenceManager.getString(Constants.P_ADDRESS));
    }

    private void CreatepopUpwindowChangePass(int gravity, FirebaseUser firebaseUser) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Cbinding = PopupChangePasswordBinding.inflate(getLayoutInflater());
        dialog.setContentView(Cbinding.getRoot());


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




        Cbinding.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Cbinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPass = Cbinding.txtcurrentPass.getText().toString();
                String newPass =  Cbinding.txtnewPass.getText().toString();
                //String confirmPass = Cbinding.txtconfirmPass.getText().toString();
                String email = firebaseUser.getEmail();

                if (!checkValidInputPassword()) {
                    Toast.makeText(getActivity(), "Failed ", Toast.LENGTH_SHORT).show();
                    return;
                }


                AuthCredential credential = EmailAuthProvider.getCredential(email, currentPass);
                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Password failed", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    if(checkMatchNewPassword()){
                                        firebaseUser.updatePassword(newPass)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("Update pass", "User password updated.");
                                                            Toast.makeText(getActivity(), "User password updated successfull.", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                        else{
                                                            Log.d("Update pass", "User password updated failed.");
                                                            Toast.makeText(getActivity(), "User password updated failed.", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                    }else{
                                        Toast.makeText(getActivity(), "Password does not match.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
            }
            // }
        });

        dialog.show();

    }

    private boolean checkMatchNewPassword(){
        if (Cbinding.txtnewPass.getText().toString()
                .equals(Cbinding.txtconfirmPass.getText().toString())){
            Log.i("Not match password", "true");
            return true;
        }
        return false;
    }

    private boolean checkValidInputPassword() {
        if (Cbinding.txtnewPass.getText().toString().trim().isEmpty()
                || Cbinding.txtconfirmPass.getText().toString().trim().isEmpty()
                || Cbinding.txtcurrentPass.getText().toString().trim().isEmpty())
            return false;
        else return true;
    }

    private boolean checkValidInputEProfile() {
        if (Ebinding.txtfullName.getText().toString().trim().isEmpty()
                || Ebinding.txtMobile.getText().toString().trim().isEmpty()
                || Ebinding.txtAddress.getText().toString().trim().isEmpty())
            return false;
        else return true;
    }

}