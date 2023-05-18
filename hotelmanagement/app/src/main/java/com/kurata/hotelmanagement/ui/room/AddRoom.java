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
import com.google.firebase.auth.FirebaseAuth;
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
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Room object =new Room();

    //Value spinner
//    private String shoteltype;
//    private String shotel;
//    private String sroomtype;

    private String shoteltypeID, sroomtypeID, shotelID;

    //save data
    private Preference preferenceManager;
    //Image
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    ArrayList<String> ImagesUrl;
    Boolean count = false;
    //spinner status
    ArrayAdapter<String> adapterItems;
    private String[] items =  {"Activate","Disable"};
    Boolean status=false;

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

        //save data
        preferenceManager = new Preference(getApplicationContext());

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




        //visible
        if (extras.get("id_cu")!=null){
            binding.btnEdit.setVisibility(View.INVISIBLE);
            binding.btnDelete.setVisibility(View.INVISIBLE);
            binding.btnSubmit.setVisibility(View.VISIBLE);
            binding.UploadImage.setImageResource(R.drawable.uploading_dark);

            getDataRoomtype(null, false);
            getDataHoteltype(null, null,false);

            if(preferenceManager.getString(Constants.P_ROLE).equals("Customer")){
                binding.outlinedHotel.setVisibility(View.VISIBLE);
                binding.outlinedHoteltype.setVisibility(View.GONE);
                getDataHotelCustomer(FirebaseAuth.getInstance().getUid(), null);
            }

            //get value filter
        }
        else{
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            object = (Room) args.getSerializable("model");
            binding.outlinedHotel.setVisibility(View.VISIBLE);
            binding.UploadImage.setImageResource(R.drawable.uploading_dark);
            binding.viewPager.setVisibility(View.VISIBLE);
            binding.txtName.setText(object.getName());
            binding.txtabout.setText(object.getAbout());
            binding.txtprice.setText(object.getPrice());
            binding.txtsum.setText(object.getSum());
            binding.txtremaining.setText(object.getRemai());
            ImagesUrl = object.getImage();
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, ImagesUrl);
            binding.viewPager.setAdapter(adapter);
            if(object.getStatus()){
                binding.status.setText(items[0]);
            }else{
                binding.status.setText(items[1]);
            }
            if(preferenceManager.getString(Constants.P_ROLE).equals("Customer")){
                binding.outlinedHotel.setVisibility(View.VISIBLE);
                binding.outlinedHoteltype.setVisibility(View.GONE);
                getDataHotelCustomer(FirebaseAuth.getInstance().getUid(), object.getHotel_id());
                getDataRoomtype(object.getRoomtype_id(), true);
            }else{
                getDataRoomtype(object.getRoomtype_id(), true);
                getDataHoteltype(object.getHotel_id(), object.getHoteltype_id(),true);
            }


            //delete
            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteRoom(firestore, object.getId());
                }
            });

        }

        //choose image --> permission
        binding.UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = true;
                CheckPermission();
            }
        });

        //set data spinner status
        adapterItems = new ArrayAdapter<String>(this, R.layout.drop_down_item, items);
        binding.status.setAdapter(adapterItems);

        binding.status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if(item.equals(items[0])){
                    status = true;
                }
            }
        });

        //Submit
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadIMages(sroomtypeID);
            }
        });

        //edit
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count){
                    UpdateIMages(shotelID, object.getId());
                    Log.d("test", "hsdgfgghsdf");
                }else{
                    Edit(object.getImage(), object.getRoomtype_id(), object.getId());
                }

            }
        });

        //On back
        binding.back.setOnClickListener(v -> onBackPressed());

    }
    // get value Spinner
    private void getDataRoomtype(String ID, Boolean mode){
        rViewModel = new ViewModelProvider(this).get(RoomTypeViewModel.class);
        rViewModel.init();

        ArrayAdapter<Roomtype> adapter = new ArrayAdapter<Roomtype>(this, R.layout.drop_down_item, roomtype);
        binding.mRoomtype.setAdapter(adapter);

        binding.mRoomtype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Roomtype item = (Roomtype) adapterView.getItemAtPosition(i);
                sroomtypeID = item.getId();
            }
        });
        rViewModel.getAllRoomtypeData().observe(this, roomtypes  -> {
            roomtype.clear();
            roomtype.addAll(roomtypes);
            if(mode){
                String sroomtypeName = null;
                Roomtype tmp;
                //String roomName = null;
                for(int i=0; i < roomtypes.size(); i++){
                    tmp = roomtypes.get(i);
                    if(ID.equals(tmp.getId())){
                        sroomtypeID = tmp.getId();
                        sroomtypeName = tmp.getName();
                    }
                }
                binding.mRoomtype.setText(sroomtypeName,false);
            }
            adapter.notifyDataSetChanged();
        });
    }

    //todo -get data hoteltype
    private void getDataHoteltype(String hotelId, String hoteltypeID, Boolean mode){

        zViewModel = new ViewModelProvider(this).get(HoteltypesViewModel.class);
        zViewModel.init();

        ArrayAdapter<Hoteltype> adapter = new ArrayAdapter<Hoteltype>(this, R.layout.drop_down_item, hoteltype);
        binding.mHoteltype.setAdapter(adapter);

        binding.mHoteltype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hoteltype item = (Hoteltype) adapterView.getItemAtPosition(i);
                shoteltypeID = item.getId();
                binding.outlinedHotel.setVisibility(View.VISIBLE);
                getDataHotel(shoteltypeID, hotelId, mode);

            }
        });

        zViewModel.getHoteltypeActivateData().observe(this, hoteltypes  -> {
            hoteltype.clear();
            hoteltype.addAll(hoteltypes);
            if(mode){
                //binding.mHotel.setText(" ", false);
                String shoteltypeName = null;
                Hoteltype tmp;
                //String roomName = null;
                for(int i=0; i < hoteltypes.size(); i++){
                    tmp = hoteltypes.get(i);
                    if(hoteltypeID.equals(tmp.getId())){
                        shoteltypeID = tmp.getId();
                        shoteltypeName = tmp.getName();
                    }
                }
                binding.outlinedHotel.setVisibility(View.VISIBLE);
                getDataHotel(shoteltypeID, hotelId, mode);
                binding.mHoteltype.setText(shoteltypeName,false);
            }
            adapter.notifyDataSetChanged();
        });
    }
    //todo -get data hotel
    private void getDataHotel(String uid, String ID, Boolean mode){
        hViewModel =  new ViewModelProvider(this).get(HotelViewModel.class);
        hViewModel.init();

        ArrayAdapter<Hotel> adapter = new ArrayAdapter<Hotel>(this, R.layout.drop_down_item, hotel);
        binding.mHotel.setAdapter(adapter);

        binding.mHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hotel item = (Hotel) adapterView.getItemAtPosition(i);
                shotelID = item.getId();
            }
        });
        hViewModel.getHotelActivateData(uid).observe(this, hotelModels -> {
            hotel.clear();
            hotel.addAll(hotelModels);
            if (mode){
                String shotelName = null;
                Hotel tmp;
                //String roomName = null;
                for(int i=0; i < hotelModels.size(); i++){
                    tmp = hotelModels.get(i);
                    if(ID.equals(tmp.getId())){
                        shotelID = tmp.getId();
                        shotelName = tmp.getName();
                    }
                }
                binding.mHotel.setText(shotelName,false);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void getDataHotelCustomer(String uid, String ID){
        hViewModel =  new ViewModelProvider(this).get(HotelViewModel.class);
        hViewModel.init();

        ArrayAdapter<Hotel> adapter = new ArrayAdapter<Hotel>(this, R.layout.drop_down_item, hotel);
        binding.mHotel.setAdapter(adapter);

        binding.mHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hotel item = (Hotel) adapterView.getItemAtPosition(i);
                shotelID = item.getId();
                shoteltypeID = item.getHoteltypeID();
            }
        });
        hViewModel.getHotelActivateCustomer(uid).observe(this, hotelModels -> {
            hotel.clear();
            hotel.addAll(hotelModels);
            if(ID !=null){
                String shotelName = null;
                Hotel tmp;
                for(int i=0; i < hotelModels.size(); i++){
                    tmp = hotelModels.get(i);
                    if(ID.equals(tmp.getId())){
                        shotelID = tmp.getId();
                        shoteltypeID = tmp.getHoteltypeID();
                        shotelName = tmp.getName();
                    }
                }
                binding.mHotel.setText(shotelName,false);
            }
            adapter.notifyDataSetChanged();
        });
    }

    //get data index spinner


    //todo - Delete room
    private void DeleteRoom(FirebaseFirestore firestore, String id){
        firestore.collection("room").document(id)
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
        String Sum = binding.txtsum.getText().toString();
        String Remai = binding.txtremaining.getText().toString();
        if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Price) && !TextUtils.isEmpty(About)  &&ImageUri != null) {
            // now we need a model class
            Room model = new Room("", Name, Price , About , shoteltypeID , shotelID , sroomtypeID , urlsList, status, Sum, Remai);
            firestore.collection("room").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // now here we need Item id and set into model
                    model.setId(documentReference.getId());
                    firestore.collection("room").document(model.getId())
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
    //TODO - EDIT
    private void UpdateIMages(String uid, String roomID) {
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
                                   Edit(UrlsList, uid, roomID);
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


    private void Edit(ArrayList<String> urlsList, String uid, String ID) {
        // now we need get text from EditText
        String Name = binding.txtName.getText().toString();
        String Price = binding.txtprice.getText().toString();
        String About= binding.txtabout.getText().toString();
        String Sum = binding.txtsum.getText().toString();
        String Remai = binding.txtremaining.getText().toString();
        if(binding.status.getText().toString().equals(items[0])){
            status = true;
        }
        if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Price) && !TextUtils.isEmpty(About)) {
            // now we need a model class
            //Room model = new Room("", Name, Price , About , shoteltype , shotel , sroomtype , urlsList, item);

            Map<String, Object> update = new HashMap<>();
            update.put("name", binding.txtName.getText().toString());
            update.put("id", ID);
            update.put("price", Price);
            update.put("about", About);
            update.put("roomtype_id", sroomtypeID);
            update.put("hoteltype_id", shoteltypeID);
            update.put("hotel_id", shotelID);
            update.put("image",urlsList);
            update.put("status",status);
            update.put("sum",Sum);
            update.put("remai",Remai);

            DocumentReference docref = firestore.collection("room").document(ID);

            docref.update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    // if data uploaded successfully then show toast
                    Toast.makeText(AddRoom.this, "Your data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    SystemClock.sleep(500);
                    onBackPressed();
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