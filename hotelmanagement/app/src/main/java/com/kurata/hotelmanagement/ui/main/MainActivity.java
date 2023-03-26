package com.kurata.hotelmanagement.ui.main;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.ui.home.Home;
import com.kurata.hotelmanagement.ui.manage.manage;
import com.kurata.hotelmanagement.ui.profile.Profile;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    BottomNavigationView navigationView;
//    ActivityMainBinding binding;
    Profile fragmentProfile = new Profile();
    manage fragmentManage = new manage();
    Home fragmentHome = new Home();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String email = "huongmya53@gmail.com";
        String password = "Kirata1909";

        mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

//                            updateUI(user);
//                        }
//                        } else {
//                            Log.w(TAG, "linkWithCredential:failure", task.getException());
//                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                       }
                    }
                });

        navigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentManage).commit();
        BadgeDrawable badgeDrawable = navigationView.getOrCreateBadge(R.id.nav_home);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(10);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
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


//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//
//        replaceFragment(new manage());
//        binding.bottomNavigation.setBackground(null);
//
//        binding.bottomNavigation.setOnItemSelectedListener(item -> {
//            switch(item.getItemId()){
//                case R.id.nav_home:
//                    replaceFragment(new Profile());
//                    break;
//                case R.id.nav_manage:
//                    replaceFragment(new manage());
//                    break;
//            }
//            return true;
//        });


//
//        mAuth = FirebaseAuth.getInstance();
//        mLogout = (Button) findViewById(R.id.btnLogout);
//        mLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAuth.signOut();
//                Intent intent = new Intent(MainActivity.this, activity_login.class);
//                startActivity(intent);
//                finish();
//                Toast.makeText(MainActivity.this, "Logout successful.", Toast.LENGTH_SHORT).show();
//            }
//        });

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);
//
//// Add a new document with a generated ID
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
    }
}