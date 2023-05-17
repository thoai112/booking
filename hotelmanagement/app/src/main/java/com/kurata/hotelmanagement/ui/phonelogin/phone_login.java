package com.kurata.hotelmanagement.ui.phonelogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kurata.hotelmanagement.databinding.ActivityPhoneLoginBinding;
import com.kurata.hotelmanagement.ui.login.activity_login;
import com.kurata.hotelmanagement.ui.otp.activity_otp;

import java.util.concurrent.TimeUnit;

public class phone_login extends AppCompatActivity {

//    private SessionManager session;

    private ActivityPhoneLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityPhoneLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



//        session =new SessionManager(getApplicationContext());
//        if(session.isLoggedIn()){
//            Intent intent = new Intent(phone_login.this, activity_otp.class);
//            startActivity(intent);
//            finish();
//        }
//        session =new SessionManager(getApplicationContext());

        binding.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_login.class);
                startActivity(intent);
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            //toast error
            if(binding.txtphone.getText().toString().isEmpty()){
                Toast.makeText(phone_login.this, "Enter mobile", Toast.LENGTH_SHORT).show();
                return;
            }
            //set visibility
            binding.btnLogin.setVisibility(View.INVISIBLE);
            binding.bar.setVisibility(View.VISIBLE);
            //verify phone number
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder()
                            .setPhoneNumber("+84"+binding.txtphone.getText().toString())
                            .setTimeout(60L,TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    binding.bar.setVisibility(View.GONE);
                                    binding.btnLogin.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    binding.bar.setVisibility(View.GONE);
                                    binding.btnLogin.setVisibility(View.VISIBLE);
                                    Toast.makeText(phone_login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    binding.bar.setVisibility(View.GONE);
                                    binding.btnLogin.setVisibility(View.VISIBLE);
                                    //action
                                    Intent intent = new Intent(getApplicationContext(), activity_otp.class);
                                    intent.putExtra("mobile",binding.txtphone.getText().toString());
                                    intent.putExtra("verificationId",verificationId);
                                    startActivity(intent);
                                }
                            })
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
    }
}