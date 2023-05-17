package com.kurata.hotelmanagement.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.databinding.ActivityMainBinding;
import com.kurata.hotelmanagement.ui.home.Home;
import com.kurata.hotelmanagement.ui.manage.manage;
import com.kurata.hotelmanagement.ui.profile.Profile;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    BottomNavigationView navigationView;
    Profile fragmentProfile = new Profile();
    manage fragmentManage = new manage();
    Home fragmentHome = new Home();

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String email = "huongmya53@gmail.com";
        String password = "Kirata1909";

        //mAuth = FirebaseAuth.getInstance();

        //AuthCredential credential = EmailAuthProvider.getCredential(email, password);

//        mAuth.getCurrentUser().linkWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "linkWithCredential:success");
//                            FirebaseUser user = task.getResult().getUser();

//                            updateUI(user);
//                        }
//                        } else {
//                            Log.w(TAG, "linkWithCredential:failure", task.getException());
//                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });

        navigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentManage).commit();
        BadgeDrawable badgeDrawable = navigationView.getOrCreateBadge(R.id.nav_home);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(10);


//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_manage, R.id.nav_profile)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentHome).commit();
                        break;
                    case R.id.nav_manage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentManage).commit();
                        break;
                    case R.id.nav_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentProfile).commit();
                        break;
                }
                return true;
            }
        });
    }
}