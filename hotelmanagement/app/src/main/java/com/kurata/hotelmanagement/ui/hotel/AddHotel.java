package com.kurata.hotelmanagement.ui.hotel;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.ViewPagerAdapter;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.databinding.ActivityAddHotelBinding;
import com.kurata.hotelmanagement.databinding.PopupEditProfileBinding;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.ArrayList;

public class AddHotel extends AppCompatActivity {

    private ActivityAddHotelBinding binding;
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    ArrayList<String> ImagesUrl;
    FirebaseFirestore firestore;
    StorageReference storagereference;
    FirebaseStorage mStorage;
    ProgressDialog progressDialog;
    PopupEditProfileBinding Ebinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Bundle extras = getIntent().getExtras();

        //Init
        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();
        ImagesUrl = new ArrayList<>();

        //Load data item

        binding = ActivityAddHotelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait While Uploading Your data...");

        //firebase
        firestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storagereference = mStorage.getReference();

        //visible
        if (extras.get("id_cu")!=null){
            binding.btnEdit.setVisibility(View.INVISIBLE);
            binding.btnDelete.setVisibility(View.INVISIBLE);
            binding.btnSubmit.setVisibility(View.VISIBLE);
            binding.UploadImage.setImageResource(R.drawable.uploading_dark);
            //choose image --> permission
            binding.UploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //binding.UploadImage.setVisibility(View.INVISIBLE);
                    //binding.viewPager.setVisibility(View.VISIBLE);
                    CheckPermission();
                }
            });
        }
        else{
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            Hotel object = (Hotel) args.getSerializable("model");

            binding.txtName.setText(object.getName());
            binding.txtaddress.setText(object.getAddress());
            binding.txtstatus.setText(object.getStatus());
            binding.txtabout.setText(object.getAbout());
            binding.viewPager.setVisibility(View.VISIBLE);


            ImagesUrl = object.getImage();
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, ImagesUrl);
            binding.viewPager.setAdapter(adapter);


            //choose image --> permission
            binding.UploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //binding.UploadImage.setVisibility(View.INVISIBLE);
                    //binding.viewPager.setVisibility(View.VISIBLE);
                    //PopupEditHotel(Gravity.TOP);
                    CheckPermission();
                }
            });
        }

        //onBack
        binding.back.setOnClickListener(v -> onBackPressed());



        //add hotel --> firestore
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadIMages(extras.getString("id_cu"));
            }
        });

        //Edit hotel --> firestore
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupEditHotel(Gravity.TOP);
            }
        });

    }
    //Upload image --> firestore
    private void UploadIMages(String uid) {

        // we need list that images urls
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri IndividualImage = ChooseImageList.get(i);
            if (IndividualImage != null) {
                progressDialog.show();
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("hotels_picture");
                final StorageReference ImageName = ImageFolder.child("Image" + i + ": " + IndividualImage.getLastPathSegment());
                ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UrlsList.add(String.valueOf(uri));
                                if (UrlsList.size() == ChooseImageList.size()) {
                                    StoreLinks(UrlsList, uid);
                                }
                            }
                        });

                    }
                });
            } else {
                Toast.makeText(this, "Please fill All Field", Toast.LENGTH_SHORT).show();
            }
        }


    }
    //link image --> firestore
    private void StoreLinks(ArrayList<String> urlsList, String uid) {
        // now we need get text from EditText
        String Name = binding.txtName.getText().toString();
        String Address= binding.txtaddress.getText().toString();
        String About= binding.txtabout.getText().toString();
        String Status= binding.txtstatus.getText().toString();
        if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Address) && !TextUtils.isEmpty(About) && !TextUtils.isEmpty(Status) && ImageUri != null) {
            // now we need a model class
            Hotel model = new Hotel("", Name, Address, About , Status, UrlsList);
            firestore.collection("rating").document(uid).collection("hotels").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // now here we need Item id and set into model
                    model.setId(documentReference.getId());
                    firestore.collection("rating").document(uid).collection("hotels").document(model.getId())
                            .set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    // if data uploaded successfully then show ntoast
                                    Toast.makeText(AddHotel.this, "Your data Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            });

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill All field", Toast.LENGTH_SHORT).show();
        }
        // if you want to clear viewpager after uploading Images
        ChooseImageList.clear();


    }
    //Check permission up load image --> multifile
    private void CheckPermission() {

        //Storage permission
        RealRxPermission.getInstance(getApplicationContext())
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe();

        //Open gallery Intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , 1);
    }
    //Display image --> viewpager
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                ImageUri = data.getClipData().getItemAt(i).getUri();
                ChooseImageList.add(ImageUri);
                ImagesUrl.add(ImageUri.toString());
            }
            setAdapter();

        }
    }
    //viewpager
    private void setAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, ImagesUrl);
        binding.viewPager.setAdapter(adapter);
    }
    //Edit hotel
    private void Edit(String uid){
        String Name = binding.txtName.getText().toString();
        String Address= binding.txtaddress.getText().toString();
        String About= binding.txtabout.getText().toString();
        String Status= binding.txtstatus.getText().toString();


    }

    //Popup edit image hotel
    private void PopupEditHotel(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Ebinding = PopupEditProfileBinding.inflate(getLayoutInflater());
        dialog.setContentView(Ebinding.getRoot());

//        ViewEditProfile();

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

        Ebinding.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();


        dialog.show();
    }
}