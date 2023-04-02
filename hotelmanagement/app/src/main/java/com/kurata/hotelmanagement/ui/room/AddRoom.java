package com.kurata.hotelmanagement.ui.room;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.data.model.Room;
import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.databinding.ActivityAddRoomBinding;
import com.kurata.hotelmanagement.ui.hotel.HotelViewModel;
import com.kurata.hotelmanagement.ui.hoteltype.HoteltypesViewModel;
import com.kurata.hotelmanagement.ui.roomtype.RoomTypeViewModel;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.ArrayList;

public class AddRoom extends AppCompatActivity {

    private ActivityAddRoomBinding binding;
    FirebaseFirestore firestore;
    StorageReference storagereference;
    FirebaseStorage mStorage;
    ProgressDialog progressDialog;

    //spinner adapter
    ArrayList<Hoteltype> hoteltype = new ArrayList<Hoteltype>();
    ArrayList<Roomtype> roomtype = new ArrayList<Roomtype>();
    ArrayList<Hotel> hotel = new ArrayList<Hotel>();
    private HoteltypesViewModel zViewModel;
    private RoomTypeViewModel rViewModel;
    private HotelViewModel hViewModel;

    //Value spinner
    private String shoteltype;
    private String shotel;
    private String sroomtype;


    //Image
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    ArrayList<String> ImagesUrl;

    //spinner status
    ArrayAdapter<String> adapterItems;
    private String[] items =  {"Activate","Disable"};
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_room);

        Bundle extras = getIntent().getExtras();
        //Init
        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();
        ImagesUrl = new ArrayList<>();

        //Load data item
        binding = ActivityAddRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait While Uploading Your data...");

        //firebase
        firestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storagereference = mStorage.getReference();



        getDataRoomtype();
        getDataHoteltype();

        //visible
        if (extras.get("id_cu")!=null){
            binding.btnEdit.setVisibility(View.INVISIBLE);
            binding.btnDelete.setVisibility(View.INVISIBLE);
            binding.btnSubmit.setVisibility(View.VISIBLE);

            //get value filter
        }
        else{
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            Room object = (Room) args.getSerializable("model");
            binding.outlinedHotel.setVisibility(View.VISIBLE);
            binding.txtName.setText(object.getName());
            binding.txtabout.setText(object.getAbout());
            binding.txtprice.setText(object.getPrice());
            ImagesUrl = object.getImage();
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, ImagesUrl);
            binding.viewPager.setAdapter(adapter);
            binding.status.setText(object.getStatus());

            //delete
            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteRoom(firestore,object.getRoomtype_id(), object.getId());
                }
            });

        }

        //choose image --> permission
        binding.UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPermission();
            }
        });

        //set data spinner status
        adapterItems = new ArrayAdapter<String>(this, R.layout.drop_down_item, items);
        binding.status.setAdapter(adapterItems);

        binding.status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
            }
        });

        //Submit
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadIMages(sroomtype);
            }
        });

        //On back
        binding.back.setOnClickListener(v -> onBackPressed());

    }
    // get value Spinner
    private void getDataRoomtype(){
        rViewModel = new ViewModelProvider(this).get(RoomTypeViewModel.class);
        rViewModel.init();

        ArrayAdapter<Roomtype> adapter = new ArrayAdapter<Roomtype>(this, R.layout.drop_down_item, roomtype);
        binding.mRoomtype.setAdapter(adapter);

        binding.mRoomtype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Roomtype item = (Roomtype) adapterView.getItemAtPosition(i);
                sroomtype = item.getId();
            }
        });
        rViewModel.getAllRoomtypeData().observe(this, roomtypes  -> {
            roomtype.clear();
            roomtype.addAll(roomtypes);
            adapter.notifyDataSetChanged();
        });
    }

    private void getDataHoteltype(){

        zViewModel = new ViewModelProvider(this).get(HoteltypesViewModel.class);
        zViewModel.init();

        ArrayAdapter<Hoteltype> adapter = new ArrayAdapter<Hoteltype>(this, R.layout.drop_down_item, hoteltype);
        binding.mHoteltype.setAdapter(adapter);

        binding.mHoteltype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hoteltype item = (Hoteltype) adapterView.getItemAtPosition(i);
                shoteltype = item.getId();
                binding.outlinedHotel.setVisibility(View.VISIBLE);
                getDataHotel(shoteltype);
            }
        });
        zViewModel.getHoteltypeActivateData().observe(this, hoteltypes  -> {
            hoteltype.clear();
            hoteltype.addAll(hoteltypes);
            adapter.notifyDataSetChanged();
        });

    }

    private void getDataHotel(String uid){
        hViewModel =  new ViewModelProvider(this).get(HotelViewModel.class);
        hViewModel.init();


        ArrayAdapter<Hotel> adapter = new ArrayAdapter<Hotel>(this, R.layout.drop_down_item, hotel);
        binding.mHotel.setAdapter(adapter);

        binding.mHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hotel item = (Hotel) adapterView.getItemAtPosition(i);
                shotel = item.getId();
            }
        });
        hViewModel.getHotelActivateData(uid).observe(this, hotelModels -> {
            hotel.clear();
            hotel.addAll(hotelModels);
            adapter.notifyDataSetChanged();
        });


    }

    //todo - Delete room
    private void DeleteRoom(FirebaseFirestore firestore, String uid, String id){
        firestore.collection("rooms").document(uid).collection("room").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(getApplicationContext(), "Delete successfully deleted!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(getApplicationContext(), "Error deleting document.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //TODO - set + upload image --> firestore

    //Upload image --> firestore
    private void UploadIMages(String uid) {
        // we need list that images urls
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri IndividualImage = ChooseImageList.get(i);
            if (IndividualImage != null) {
                progressDialog.show();
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("rooms_picture");
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
        String Price = binding.txtprice.getText().toString();
        String About= binding.txtabout.getText().toString();
        if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Price) && !TextUtils.isEmpty(About) && !TextUtils.isEmpty(item) &&ImageUri != null) {
            // now we need a model class
            Room model = new Room("", Name, Price , About , shoteltype , shotel , sroomtype , urlsList, item);
            firestore.collection("rooms").document(uid).collection("room").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // now here we need Item id and set into model
                    model.setId(documentReference.getId());
                    firestore.collection("rooms").document(uid).collection("room").document(model.getId())
                        .set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                // if data uploaded successfully then show ntoast
                                Toast.makeText(AddRoom.this, "Your data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                SystemClock.sleep(500);
                                onBackPressed();
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
}