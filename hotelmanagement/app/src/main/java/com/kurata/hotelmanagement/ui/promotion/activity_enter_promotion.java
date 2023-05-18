package com.kurata.hotelmanagement.ui.promotion;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.kurata.hotelmanagement.databinding.ActivityEnterPromotionBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;





public class activity_enter_promotion extends AppCompatActivity {

    private ActivityEnterPromotionBinding binding;
    final Calendar myCalendar = Calendar.getInstance();
    long lTimeStart, lTimeEnd;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityEnterPromotionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent().getBooleanExtra("promotion",false)){
            Log.d("txt", "test");
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
        binding.back.setOnClickListener(v-> onBackPressed());
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



}