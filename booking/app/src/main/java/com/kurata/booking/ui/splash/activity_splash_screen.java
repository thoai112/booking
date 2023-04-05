package com.kurata.booking.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kurata.booking.ui.main.MainActivity;
import com.kurata.booking.utils.NetworkUtil;
import com.kurata.booking.ui.login.Activity_login;

import com.kurata.booking.databinding.ActivitySplashScreenBinding;


public class activity_splash_screen extends AppCompatActivity implements NetworkUtil.NetworkStateReceiverListener {

    private FirebaseAuth mAuth;
    private NetworkUtil networkUtil;

    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();
        startNetworkBroadcastReceiver(this);
        mAuth =FirebaseAuth.getInstance();

//        if(mAuth.getCurrentUser() != null){
//            Intent intent = new Intent(activity_splash_screen.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }else{
//            Intent intent = new Intent(activity_splash_screen.this, Activity_login.class);
//            startActivity(intent);
//            finish();
//        }
        Intent intent = new Intent(activity_splash_screen.this, MainActivity.class);
        startActivity(intent);
        finish();


    }

    @Override
    protected void onPause() {
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    public void networkAvailable() {
        Toast.makeText(this, "back online", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkUtil = new NetworkUtil();
        networkUtil.addListener((NetworkUtil.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }

    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkUtil, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }
    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkUtil);
    }
//    }
}