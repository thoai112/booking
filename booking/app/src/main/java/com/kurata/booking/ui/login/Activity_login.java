package com.kurata.booking.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.kurata.booking.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.kurata.booking.ui.forgetpassword.Forgot_password;
import com.kurata.booking.ui.main.MainActivity;


import com.kurata.booking.databinding.ActivityLoginBinding;
import com.kurata.booking.ui.phonelogin.Phone_login;
import com.kurata.booking.ui.signup.Sign_up;

public class Activity_login extends AppCompatActivity {

    FirebaseAuth mAuth;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        mAuth = FirebaseAuth.getInstance();


        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_login.this, Forgot_password.class));
            }

        });


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });


        binding.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_login.this, Phone_login.class));
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_login.this, Sign_up.class));
            }
        });
    }


    private void loginUser() {
        String email = binding.txtemail.getText().toString();
        String password = binding.txtpassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            binding.txtemail.setError("Email cannot be empty");
            binding.txtemail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            binding.txtpassword.setError("Password cannot be empty");
            binding.txtpassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Activity_login.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Activity_login.this, MainActivity.class));

                    } else {
                        Toast.makeText(Activity_login.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("LOG",task.getException().getMessage());
                    }
                }
            });
        }
    }
}