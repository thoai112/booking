package com.kurata.hotelmanagement.ui.forgotpassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.ui.login.activity_login;

import com.kurata.hotelmanagement.databinding.ActivityForgotPasswordBinding;

public class activity_forgot_password extends AppCompatActivity {
    TextView mBack;
    Button mSendmail;
    EditText mEmail;
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
                startActivity(new Intent(activity_forgot_password.this, activity_login.class));
            }

        });


        binding.btnSendEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String userEmail = binding.Email.getText().toString();
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(activity_forgot_password.this, "An email is sent to your email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                    finish();
//                startActivity(new Intent(activity_forgot_password.this, activity_login.class));
            }

        });
    }
}