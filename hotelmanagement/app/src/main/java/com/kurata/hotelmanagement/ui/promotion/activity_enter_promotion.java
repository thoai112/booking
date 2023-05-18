package com.kurata.hotelmanagement.ui.promotion;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.data.model.Promotion;
import com.kurata.hotelmanagement.data.model.Room;
import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.databinding.ActivityEnterPromotionBinding;
import com.kurata.hotelmanagement.ui.hotel.HotelViewModel;
import com.kurata.hotelmanagement.ui.hoteltype.HoteltypesViewModel;
import com.kurata.hotelmanagement.ui.room.RoomsViewModel;
import com.kurata.hotelmanagement.ui.roomtype.RoomTypeViewModel;
import com.kurata.hotelmanagement.ui.user.UsersViewModel;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;
import com.vanniktech.rxpermission.RealRxPermission;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;





public class activity_enter_promotion extends AppCompatActivity {

    private ActivityEnterPromotionBinding binding;
    final Calendar myCalendar = Calendar.getInstance();
    long lTimeStart, lTimeEnd;
    private Uri ImageUri;
    //private Promotion object ;

    //spinner adapter -- id
    ArrayList<Hoteltype> hoteltype = new ArrayList<Hoteltype>();
    ArrayList<Roomtype> roomtype = new ArrayList<Roomtype>();
    ArrayList<Hotel> hotel = new ArrayList<Hotel>();
    ArrayList<Room> room = new ArrayList<Room>();
    ArrayList<User> user= new ArrayList<User>();
    private HoteltypesViewModel zViewModel;
    private RoomTypeViewModel rViewModel;
    private HotelViewModel hViewModel;
    private RoomsViewModel kViewModel;
    private UsersViewModel uViewModel;

    //set data
    private String shoteltypeID, sroomtypeID, shotelID;

    //save data
    private Preference preferenceManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityEnterPromotionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //save data
        preferenceManager = new Preference(getApplicationContext());

        if(preferenceManager.getString(Constants.P_ROLE).equals("Customer")){
            binding.outlinedHoteltype.setVisibility(View.GONE);
            binding.outlinedUser.setVisibility(View.GONE);
            binding.outlinedHotel.setVisibility(View.GONE);
        }

        if(getIntent().getBooleanExtra("promotion",false)){
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            Promotion object = (Promotion) args.getSerializable("model");
            Date dateStart = new Date(args.getLong("dateStart"));
            Date dateEnd = new Date(args.getLong("dateEnd"));
            Date createAt = new Date(args.getLong("createAt"));
            RenderPromotion(object,LocalDateTime.ofInstant(dateStart.toInstant(), ZoneId.systemDefault()), LocalDateTime.ofInstant(dateEnd.toInstant(), ZoneId.systemDefault()));
        }

        TimePickerDialog.OnTimeSetListener timeStart = (timePicker, i, i1) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, i);
            myCalendar.set(Calendar.MINUTE, i1);
            myCalendar.set(Calendar.SECOND, 0);
            updateLabel(binding.etDateStart);
            lTimeStart = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();

        };
        DatePickerDialog.OnDateSetListener dateStart = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            new TimePickerDialog(activity_enter_promotion.this, timeStart, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
        };

        binding.etDateStart.setOnClickListener(view -> new DatePickerDialog(activity_enter_promotion.this, dateStart, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        TimePickerDialog.OnTimeSetListener timeEnd = (timePicker, i, i1) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, i);
            myCalendar.set(Calendar.MINUTE, i1);
            myCalendar.set(Calendar.SECOND, 0);
            updateLabel(binding.etDateEnd);
            lTimeEnd = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();

        };
        DatePickerDialog.OnDateSetListener dateEnd = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            new TimePickerDialog(activity_enter_promotion.this, timeEnd, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
        };

        binding.etDateEnd.setOnClickListener(view -> new DatePickerDialog(activity_enter_promotion.this, dateEnd, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        binding.btnSaveVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dhghjsd",String.valueOf(binding.enterroomSliderSlider.getValue() / 100));
            }
        });

        binding.imgPreviewImageVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPermission();
            }
        });
        binding.back.setOnClickListener(v-> onBackPressed());

        //todo - get data spiner
        if(preferenceManager.getString(Constants.P_ROLE).equals("Admin")){
            binding.spin.setVisibility(View.GONE);
            binding.spin1.setVisibility(View.GONE);
            binding.outlinedUser.setVisibility(View.GONE);
            getDataRoomtype(null, false);
            getDataHoteltype(null, false);
            binding.alluser.setChecked(true);
            binding.allhoteltype.setChecked(true);
            binding.allroomtype.setChecked(true);
            binding.alluser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(!b){
                        binding.spin.setVisibility(View.VISIBLE);
                        binding.spin1.setVisibility(View.VISIBLE);
                        binding.outlinedUser.setVisibility(View.VISIBLE);
                    }else{
                        binding.spin.setVisibility(View.GONE);
                        binding.spin1.setVisibility(View.GONE);
                        binding.outlinedUser.setVisibility(View.GONE);
                    }
                }
            });

        }

    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd-MM-yyyy\nHH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setCalendar(LocalDateTime d) {
        myCalendar.set(Calendar.YEAR, d.getYear());
        myCalendar.set(Calendar.MONTH, d.getMonthValue());
        myCalendar.set(Calendar.DAY_OF_MONTH, d.getDayOfMonth());
        myCalendar.set(Calendar.HOUR_OF_DAY, d.getHour());
        myCalendar.set(Calendar.MINUTE, d.getMinute());
        myCalendar.set(Calendar.SECOND, 0);
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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , 1);
    }
    //Display image --> viewpager
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null ) {
            //int count = data.getClipData().getItemCount();
            ImageUri = data.getData();
            Glide.with(binding.imgPreviewImageVoucher).load(ImageUri).into(binding.imgPreviewImageVoucher);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RenderPromotion(Promotion p, LocalDateTime startDate, LocalDateTime endDate) {
        Glide.with(binding.imgPreviewImageVoucher).load(p.getImg()).into(binding.imgPreviewImageVoucher);
        binding.etVoucherTitleName.setText(p.getName());
        binding.code.setText(p.getCode());
        binding.etVoucherDescriptionName.setText(p.getDescription());
        binding.enterroomSliderSlider.setValue(Float.valueOf(p.getDiscount_ratio()) * 100);
        binding.txtsum.setText(p.getSum());
        binding.txtremaining.setText(p.getRemai());
        setCalendar(startDate);
        updateLabel(binding.etDateStart);
        lTimeStart = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();
        setCalendar(endDate);
        updateLabel(binding.etDateEnd);
        lTimeEnd = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();
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
    private void getDataHoteltype(String hoteltypeID, Boolean mode){

        zViewModel = new ViewModelProvider(this).get(HoteltypesViewModel.class);
        zViewModel.init();

        ArrayAdapter<Hoteltype> adapter = new ArrayAdapter<Hoteltype>(this, R.layout.drop_down_item, hoteltype);
        binding.mHoteltype.setAdapter(adapter);

        binding.mHoteltype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hoteltype item = (Hoteltype) adapterView.getItemAtPosition(i);
                shoteltypeID = item.getId();
            }
        });

        zViewModel.getHoteltypeActivateData().observe(this, hoteltypes  -> {
            hoteltype.clear();
            hoteltype.addAll(hoteltypes);
            if(mode){
                String shoteltypeName = null;
                Hoteltype tmp;
                for(int i=0; i < hoteltypes.size(); i++){
                    tmp = hoteltypes.get(i);
                    if(hoteltypeID.equals(tmp.getId())){
                        shoteltypeID = tmp.getId();
                        shoteltypeName = tmp.getName();
                    }
                }
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

}