package com.kurata.hotelmanagement.ui.profile;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.data.repository.Repository;
import com.kurata.hotelmanagement.databinding.FragmentProfileBinding;
import com.kurata.hotelmanagement.databinding.PopupChangePasswordBinding;
import com.kurata.hotelmanagement.databinding.PopupEditProfileBinding;
import com.kurata.hotelmanagement.ui.login.activity_login;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.HashMap;

import javax.inject.Inject;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProfileViewModel profileViewModel;
    private static final int REQUEST_CODE = 1;
    FirebaseFirestore db;
    @Inject
    RequestManager requestManager;
    FirebaseStorage storage;
    private Preference preferenceManager;
    Uri imageUri;
    FirebaseFirestore firestore;
    String currentId;
    ArrayAdapter<String> adapterItems;
    private String[] items =  {"Activate","Disable"};
    String item;


    private FragmentProfileBinding binding;
    private PopupEditProfileBinding Ebinding;
    private PopupChangePasswordBinding Cbinding;
    public Profile() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        preferenceManager = new Preference(getContext());



        storage = FirebaseStorage.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentId = firebaseUser.getUid();
        DocumentReference reference;
        firestore = FirebaseFirestore.getInstance();


        ViewModelProvider.Factory test = (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory();

        profileViewModel = new ViewModelProvider(getActivity(), test).get(ProfileViewModel.class);
        //profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
        profileViewModel.init(currentId);
        profileViewModel.getProfile().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                UpdateProfileViews(user);
            }
        });


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        binding.imageLogout .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), activity_login.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Logout successful.", Toast.LENGTH_SHORT).show();

            }
        });

        binding.Logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGallery();
            }

        });


        binding.changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreatepopUpwindowChangePass(Gravity.TOP, firebaseUser);
            }
        });

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupEditProfile(Gravity.TOP, firestore, currentId);
            }

        });

        return view;
    }


    private void UpdateProfileViews(User user ) {
        binding.fullname.setText(user.getfullName());
        binding.Email.setText(user.getEmail());
        Glide.with(this).load(user.getAvatar()).into(binding.Logo);
        binding.mMobile.setText(user.getMobile());
        binding.mRole.setText(user.getRole());
        binding.mAddress.setText(user.getAddress());
        if(user.isStatus()==Boolean.TRUE){
            binding.mStatus.setText("Activate");
        }else{
            binding.mStatus.setText("Disable");
        }


        preferenceManager.putString(Constants.P_FULL_NAME, user.getfullName() );
        preferenceManager.putString(Constants.P_EMAIL, user.getEmail() );
        preferenceManager.putString(Constants.P_ADDRESS, user.getAddress());
        preferenceManager.putString(Constants.P_MOBILE, user.getMobile() );
        preferenceManager.putString(Constants.P_ROLE, user.getRole() );
        preferenceManager.putBoolean(String.valueOf(Constants.P_STATUS), user.isStatus());
        preferenceManager.putString(Constants.P_AVATAR, user.getAvatar());
    }


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
                    Repository repository = new Repository();
                    repository.updateDisplayImage(selectedImage,firestore,currentId,storage);
                }
            }
        }
    }



    private void PopupEditProfile(int gravity, FirebaseFirestore firebaseFirestore, String uid) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Ebinding = PopupEditProfileBinding.inflate(getLayoutInflater());
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

        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.drop_down_item, items);
        Ebinding.autoCompleteTxt.setAdapter(adapterItems);

        Ebinding.autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
            }
        });

        Ebinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkValidInputEProfile()){
                    Toast.makeText(getContext(), "field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                HashMap<String, Object> user = new HashMap<>();
                user.put("fullName",Ebinding.txtfullName.getText().toString());
                user.put("email",Ebinding.txtEmail.getText().toString());
                user.put("address",Ebinding.txtAddress.getText().toString());
                user.put("mobile",Ebinding.txtMobile.getText().toString());
                user.put("role",Ebinding.txtRole.getText().toString());
                if(item =="Activate"){
                    user.put("status",Boolean.TRUE);
                }
                else{
                    user.put("status",Boolean.FALSE);
                }
               //user.put("status",Ebinding.txtStatus.getText().toString());


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
        Ebinding.txtRole.setText(preferenceManager.getString(Constants.P_ROLE));
        if(preferenceManager.getBoolean(String.valueOf(Constants.P_STATUS)) == Boolean.TRUE)
        {
            Ebinding.autoCompleteTxt.setText("Activate");
        }
        else {
            Ebinding.autoCompleteTxt.setText("Disable");
        }
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
                //|| Ebinding.txtZip.getText().toString().trim().isEmpty()
                || Ebinding.txtAddress.getText().toString().trim().isEmpty()
                || Ebinding.txtRole.getText().toString().trim().isEmpty())
            return false;
        else return true;
    }


}