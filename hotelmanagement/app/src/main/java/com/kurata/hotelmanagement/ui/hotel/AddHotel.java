package com.kurata.hotelmanagement.ui.hotel;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kurata.hotelmanagement.ui.location.Add_location;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.ViewPagerAdapter;
import com.kurata.hotelmanagement.data.model.City;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.databinding.ActivityAddHotelBinding;
import com.kurata.hotelmanagement.databinding.PopupEditProfileBinding;
import com.kurata.hotelmanagement.ui.user.UsersViewModel;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;
import com.vanniktech.rxpermission.RealRxPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Preference preferenceManager;
    GeoPoint geoPoint;
    Hotel object = new Hotel();
    //spinner
    ArrayAdapter<String> adapterItems;
    ArrayList<City> list = new ArrayList<City>();
    ArrayList<User> user = new ArrayList<User>();
    private String[] items = {"Activate","Disable"};
    Boolean item = Boolean.FALSE;
    Boolean count =Boolean.FALSE;
    String cityID, userID;
    //view model
    private HotelViewModel zViewModel;
    private UsersViewModel uViewModel;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Bundle extras = getIntent().getExtras();

        preferenceManager = new Preference(getApplicationContext());
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

            //spinner
            getCity("");
            getUser("");

            //choose image --> permission
            binding.UploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckPermission();
                }
            });

            binding.addlocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplication(), Add_location.class);
                    startActivityForResult(intent , 1);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                }
            });
        }
        else{
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            object = (Hotel) args.getSerializable("model");
            binding.txtName.setText(object.getName());
            binding.txtaddress.setText(object.getAddress());
            binding.txtabout.setText(object.getAbout());
            binding.viewPager.setVisibility(View.VISIBLE);

            if(object.getStatus() != true){
                binding.spinner.setText(items[1]);
            } else {
                binding.spinner.setText(items[0]);
                item=true;
            }
            getCity(object.getCitiID());

            if(preferenceManager.getString(Constants.P_ROLE).equals("Admin")){
                getUser(object.getUserID());
            }
            else{
                binding.outlinedUser.setVisibility(View.GONE);
            }
            ImagesUrl = object.getImage();
            //extras.getString("ht_id")
            //Log.d("GEOOOOOOOO", String.valueOf(extras.getDouble("lati",0.0)));
            //Toast.makeText(this, "GEOO"+ object.getLocation(), Toast.LENGTH_SHORT).show();
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, ImagesUrl);
            binding.viewPager.setAdapter(adapter);

            //choose image --> permission
            binding.UploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //binding.UploadImage.setVisibility(View.INVISIBLE);
                    //binding.viewPager.setVisibility(View.VISIBLE);
                    //PopupEditHotel(Gravity.TOP);
                    count = true;
                    CheckPermission();
                }
            });

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteHotel(firestore,extras.getString("ht_id"),object.getId());
                }
            });

            binding.addlocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplication(), Add_location.class);
                    Location x = new Location(LocationManager.GPS_PROVIDER);

                    Log.d("latiii", preferenceManager.getString(Constants.LATITUDE));
                    Log.d("longiii", preferenceManager.getString(Constants.LONGITUDE));

                    x.setLatitude(Double.parseDouble(preferenceManager.getString(Constants.LATITUDE)));
                    x.setLongitude(Double.parseDouble(preferenceManager.getString(Constants.LONGITUDE)));

                    intent.putExtra("location", (Parcelable) x);
                    intent.putExtra("address",binding.txtaddress.getText().toString());
                    startActivityForResult(intent , 1);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                }
            });

        }


        adapterItems = new ArrayAdapter<String>(this, R.layout.drop_down_item, items);
        binding.spinner.setAdapter(adapterItems);

        binding.spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String data = adapterView.getItemAtPosition(i).toString();
                binding.spinner.setText(data);
                if (data.equals(items[0])){
                    item=true;
                }
            }
        });

        //onBack
        binding.back.setOnClickListener(v -> onBackPressed());


        //add hotel --> firestore
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadIMages(extras.getString("id_cu"));
            }
        });


        //Edit hotel --> firestoreHotel object
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count){
                    UpdateIMages(object.getImage(),object.getHoteltypeID(),object.getId());
                }
                else{
                    Edit(object.getImage(),object.getHoteltypeID(),object.getId());
                }

            }
        });

    }
    //get citi
    private void getCity(String ID) {
        zViewModel = new ViewModelProvider(this).get(HotelViewModel.class);
        zViewModel.init();

        ArrayAdapter<City> adapter = new ArrayAdapter<City>(getApplicationContext(), R.layout.drop_down_item, list);
        binding.citi.setAdapter(adapter);

        binding.citi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                City item = (City) adapterView.getItemAtPosition(i);
                cityID = item.getId();
            }
        });

        zViewModel.getCities().observe(this, hotelModels -> {
            list.clear();
            list.addAll(hotelModels);
            City tmp;
            String cityName = null;
            for(int i=0; i < hotelModels.size(); i++){
                tmp = hotelModels.get(i);
                if(ID.equals(tmp.getId())){
                    cityID = tmp.getId();
                    cityName = tmp.getName();
                }
            }
            binding.citi.setText(cityName,false);
            adapter.notifyDataSetChanged();
        });
    }

    //get userid
    private void getUser(String ID) {
        uViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        uViewModel.init();

        ArrayAdapter<User> adapter = new ArrayAdapter<User>(getApplicationContext(), R.layout.drop_down_item, user);
        binding.user.setAdapter(adapter);

        binding.user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User item = (User) adapterView.getItemAtPosition(i);
                userID = item.getId();
            }
        });

        uViewModel.getAllUsersData(FirebaseAuth.getInstance().getUid(),"Customer").observe(this, users -> {
            user.clear();
            user.addAll(users);
            User tmp;
            String userName = null;
            for(int i=0; i < users.size(); i++){
                tmp = users.get(i);
                if(ID.equals(tmp.getId())){
                    userID = tmp.getId();
                    userName = tmp.getfullName();
                }
            }
            binding.user.setText(userName,false);
            adapter.notifyDataSetChanged();
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

    //update data
    private void UpdateIMages(ArrayList<String> img,String uid, String ID) {
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
                                Log.d("imagexxxxxxx", UrlsList.toString());
                                Log.d("imageooooooo", img.toString());
                                //img.add(String.valueOf(uri));
                                if (UrlsList.size() == ChooseImageList.size()) {
                                    Log.d("imagevdffgd", UrlsList.toString());
                                    Log.d("imagedasjhsj", img.toString());
                                    Edit(UrlsList, uid, ID);
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
        //Boolean status = Boolean.FALSE;

        if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Address) && !TextUtils.isEmpty(About)  && ImageUri != null) {
            // now we need a model class
            Hotel model = new Hotel("", Name, Address, About, item, geoPoint, userID, cityID, uid, urlsList);
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


        }else
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                binding.txtaddress.setText(data.getStringExtra("address"));
                geoPoint = new GeoPoint(Double.parseDouble(preferenceManager.getString(Constants.LATITUDE)), Double.parseDouble(preferenceManager.getString(Constants.LONGITUDE)));
            }
        }
    }

    //viewpager
    private void setAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, ImagesUrl);
        binding.viewPager.setAdapter(adapter);
    }

    //Edit hotel
    private void Edit(ArrayList<String> urlsList, String uid, String ID) {
        // now we need get text from EditText
        String Name = binding.txtName.getText().toString();
        String Address= binding.txtaddress.getText().toString();
        String About= binding.txtabout.getText().toString();

        Log.d("url", urlsList.toString());
        if (!TextUtils.isEmpty(binding.txtName.getText().toString())) {
            // now we need a model class
            geoPoint = new GeoPoint(Double.parseDouble(preferenceManager.getString(Constants.LATITUDE)), Double.parseDouble(preferenceManager.getString(Constants.LONGITUDE)));

            Hotel model = new Hotel(ID, Name, Address, About, item, geoPoint, userID, cityID, uid, urlsList);

            Map<String, Object> update = new HashMap<>();
            update.put("name", binding.txtName.getText().toString());
            update.put("id", ID);
            update.put("address", binding.txtaddress.getText().toString());
            update.put("about", binding.txtabout.getText().toString());
            update.put("hoteltypeID", uid);
            update.put("userID", userID);
            update.put("citiID", cityID);
            update.put("location", geoPoint);
            update.put("image",urlsList);
            update.put("status",item);

            DocumentReference docref = firestore.collection("rating").document(uid).collection("hotels").document(ID);

                docref.update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        // if data uploaded successfully then show toast
                        Toast.makeText(AddHotel.this, "Your data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        SystemClock.sleep(500);
                        onBackPressed();
                    }
                });
            }
//        });

     else {
        progressDialog.dismiss();
        Toast.makeText(this, "Please Fill All field", Toast.LENGTH_SHORT).show();
    }
    // if you want to clear viewpager after uploading Images
        ChooseImageList.clear();
    }

    //get geopoint from location
    private GeoPoint geoPointFromLocation(Location location) {
        GeoPoint geo = new GeoPoint(location.getLatitude(),location.getLongitude());
        return geo ;
    }

    //todo  - delete
    private void DeleteHotel(FirebaseFirestore firestore, String uid, String id){
        firestore.collection("rating").document(uid).collection("hotels").document(id)
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

}