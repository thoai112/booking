package com.kurata.booking.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.kurata.booking.R;
import com.kurata.booking.databinding.ActivityLoginBinding;
import com.kurata.booking.databinding.ActivitySignUpBinding;
import com.kurata.booking.ui.login.Activity_login;

public class Sign_up extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding.txtBack.setOnClickListener(v-> onBackPressed());
//        binding.txtBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Sign_up.this, Activity_login.class));
//            }
//        });


    }
}