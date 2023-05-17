package com.kurata.hotelmanagement.ui.promotion;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.databinding.ActivityEnterPromotionBinding;

public class activity_enter_promotion extends AppCompatActivity {

    private ActivityEnterPromotionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_enter_promotion);
    }
}