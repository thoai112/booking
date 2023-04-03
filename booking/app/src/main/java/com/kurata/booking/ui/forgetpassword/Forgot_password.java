package com.kurata.booking.ui.forgetpassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kurata.booking.R;
import com.kurata.booking.ui.login.Activity_login;

import com.kurata.booking.databinding.ActivityForgotPasswordBinding;

public class Forgot_password extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_forgot_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Forgot_password.this, Activity_login.class));
            }

        });

        binding.btnSendEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String userEmail = binding.Email.getText().toString();
                if( !TextUtils.isEmpty(userEmail)){
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Forgot_password.this, "An email is sent to your email", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    finish();
                }
                else{
                    binding.Email.setError("Email cannot be empty");
                    binding.Email.requestFocus();
                }
//                startActivity(new Intent(activity_forgot_password.this, activity_login.class));
            }

        });
    }
}