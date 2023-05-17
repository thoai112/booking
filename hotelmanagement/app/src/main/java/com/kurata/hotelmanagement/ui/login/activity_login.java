package com.kurata.hotelmanagement.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.databinding.ActivityLoginBinding;
import com.kurata.hotelmanagement.ui.forgotpassword.activity_forgot_password;
import com.kurata.hotelmanagement.ui.main.MainActivity;
import com.kurata.hotelmanagement.ui.phonelogin.phone_login;
import com.kurata.hotelmanagement.ui.profile.ProfileViewModel;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;

public class activity_login extends AppCompatActivity {
    TextView mForgot;
    TextInputEditText editTextEmail, editTextPassword;
    Button mLogin;
    ImageView mPhone;
    FirebaseAuth mAuth;
    private ActivityLoginBinding binding;
    private Preference preferenceManager;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //save data
        preferenceManager = new Preference(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.txtemail);
        editTextPassword = findViewById(R.id.txtpassword);
        mForgot = (TextView) findViewById(R.id.txtForgotPassword);
        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_login.this, activity_forgot_password.class));
            }

        });

        mLogin = (Button) findViewById(R.id.btnLogin);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        mPhone = findViewById(R.id.phone);
        mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_login.this, phone_login.class));
            }
        });
    }


    private void loginUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email cannot be empty");
            editTextEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password cannot be empty");
            editTextPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        getRole(firebaseUser.getUid());

                    } else {
                        Toast.makeText(activity_login.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("LOG",task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void getRole(String currentId){
        preferenceManager = new Preference(getApplicationContext());
        ViewModelProvider.Factory test = (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory();
        profileViewModel = new ViewModelProvider(getViewModelStore(), test).get(ProfileViewModel.class);
        profileViewModel.init(currentId);
        profileViewModel.getProfile().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                //Update(user);
                if (user.getRole().equals("User") || (user.isStatus() != true)){
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Toast.makeText(activity_login.this, "User does not permision or disable", Toast.LENGTH_SHORT).show();
                } else{
                    preferenceManager.putString(Constants.P_ROLE, user.getRole());
                    Log.d("role test", preferenceManager.getString(Constants.P_ROLE));
                    Toast.makeText(activity_login.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(activity_login.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

}