package com.kurata.booking.ui.signup;

import static android.content.ContentValues.TAG;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.booking.databinding.ActivitySignUpBinding;
import com.kurata.booking.R;
import com.kurata.booking.data.model.User;
import com.kurata.booking.databinding.ActivitySignUpBinding;
import com.kurata.booking.ui.login.Activity_login;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Sign_up extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String verificationId, UUID;
    private Boolean check = FALSE;


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

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckValue();
                if (!binding.cb.isChecked()){
                    binding.cb.setError("Agree policy");
                    binding.cb.requestFocus();
                }else if (!checkPass(binding.pass.getText().toString(), binding.repass.getText().toString())){
                    binding.repass.setError("Re-Enter Password does not match");
                    binding.repass.requestFocus();
                }else if(check){
                    AuthCredential credential = EmailAuthProvider.getCredential(binding.Email.getText().toString(), binding.pass.getText().toString());
                    Checkotp(credential);


                }else{
                    binding.code.setError("CODE does not match");
                    binding.code.requestFocus();
                }

            }
        });

        binding.sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.Mobile.getText().toString())){
                    binding.Mobile.setError("Mobile cannot be empty");
                    binding.Mobile.requestFocus();
                }
                else{
                    sendcode(binding.Mobile.getText().toString());
                    check = TRUE;
                    new CountDownTimer(30000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            binding.sendcode.setVisibility(View.GONE);
                            binding.time.setVisibility(View.VISIBLE);
                            binding.time.setText("00 : " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            binding.time.setVisibility(View.GONE);
                            binding.sendcode.setVisibility(View.VISIBLE);

                        }
                    }.start();

                }
            }
        });

    }

    public void CheckValue(){
        String name = binding.Name.getText().toString();
        String email = binding.Email.getText().toString();
        String address = binding.Address.getText().toString();
        String mobile = binding.Mobile.getText().toString();
        String pass = binding.pass.getText().toString();
        String repass = binding.repass.getText().toString();

        if (TextUtils.isEmpty(name)) {
            binding.Name.setError("FulllName cannot be empty");
            binding.Name.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            binding.Email.setError("Email cannot be empty");
            binding.Email.requestFocus();
        }else if (TextUtils.isEmpty(address)) {
            binding.Address.setError("Address cannot be empty");
            binding.Address.requestFocus();
        }else if (TextUtils.isEmpty(mobile)) {
            binding.Mobile.setError("Mobile cannot be empty");
            binding.Mobile.requestFocus();
        }else if (TextUtils.isEmpty(pass)) {
            binding.pass.setError("Password cannot be empty");
            binding.pass.requestFocus();
        }else if (TextUtils.isEmpty(repass)) {
            binding.repass.setError("Re-Enter Password cannot be empty");
            binding.repass.requestFocus();
        }

    }

    public void sendcode(String phone){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber("+84"+phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(Sign_up.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(Sign_up.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public void Checkotp(AuthCredential credential){

        if(verificationId !=null){
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId,
                    binding.code.getText().toString()
            );

            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
//                        binding.mbar.setVisibility(View.GONE);
//                        binding.veryOTP.setVisibility(View.VISIBLE);
                        if(task.isSuccessful()){
                            UUID = task.getResult().getUser().getUid();
                            linkAccount(credential);
                            Log.d("UUID", UUID);
                        }else{
                            binding.code.setError("CODE does not match");
                            binding.code.requestFocus();
                        }
                    });

        }
    }

    public void linkAccount(AuthCredential credential)
    {
        if(UUID!=null){
            Signup(UUID);
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Sign_up.this, Activity_login.class));
                                finish();
                                Log.d(TAG, "linkWithCredential:success");
                            } else {
                                Log.w(TAG, "linkWithCredential:failure", task.getException());
                                Toast.makeText(Sign_up.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public Boolean checkPass(String pass , String repass){
        if (!pass.equals(repass)) {
            return FALSE;
        }
        return TRUE;
    }

    public void Signup(String uid){
        User user = new User();
        user.setId(uid);
        user.setEmail(binding.Email.getText().toString());
        user.setfullName(binding.Name.getText().toString());
        user.setAddress(binding.Address.getText().toString());
        user.setMobile(binding.Mobile.getText().toString());
        user.setRole("User");
        user.setStatus(TRUE);
        user.setAvatar("");

        firestore.collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("aADDD", "goooooooood");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("hhhhh", "failedddddddddddddd");
            }
        });


    }
}