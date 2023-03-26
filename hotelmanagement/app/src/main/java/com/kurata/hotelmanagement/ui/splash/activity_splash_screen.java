package com.kurata.hotelmanagement.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kurata.hotelmanagement.ui.main.MainActivity;
import com.kurata.hotelmanagement.utils.NetworkUtil;
import com.kurata.hotelmanagement.ui.login.activity_login;

import com.kurata.hotelmanagement.databinding.ActivitySplashScreenBinding;


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

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(activity_splash_screen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(activity_splash_screen.this, activity_login.class);
            startActivity(intent);
            finish();
        }
//        if (AppUtil.isNetworkAvailable(this)){
//            Thread thread = new Thread()
//            {
//                public void run()
//                {
//                        try {
//                            sleep(2000);
//
//                        }
//                        catch (InterruptedException e)
//                        {
//                            e.printStackTrace();
//                        }
//                        finally {
//                            if(mAuth.getCurrentUser() != null){
//                                Intent intent = new Intent(activity_splash_screen.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }else{
//                                Intent intent = new Intent(activity_splash_screen.this, activity_login.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                            //startActivity(new Intent(activity_splash_screen.this, activity_login.class));
//                        }
//
//
//                }
//            };
//            thread.start();
//        }else{
//            Toast.makeText(this,"Network disconnected", Toast.LENGTH_SHORT).show();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(activity_splash_screen.this,phone_login.class));
//                    finish();
//                }
//            }, 2000);
//        }

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