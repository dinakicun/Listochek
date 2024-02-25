package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton userButton;

    HomeFragment homeFragment;
    WaterFragment waterFragment;
    CaloriesFragment caloriesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        homeFragment = new HomeFragment();
        waterFragment = new WaterFragment();
        caloriesFragment = new CaloriesFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        userButton= findViewById(R.id.main_user_btn);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_water){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, waterFragment).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.menu_home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit();
                    return true;
                }
                else if(item.getItemId() == R.id.menu_calories){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, caloriesFragment).commit();
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

    }
}