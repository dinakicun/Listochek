package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdminMainActivity extends AppCompatActivity {
    TextView nameText, emailText;
    private LinearLayout exit, dishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        exit = findViewById(R.id.exitLL);
        dishes = findViewById(R.id.DishesLL);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");

        if (name != null && email != null) {
            nameText.setText(name);
            emailText.setText("Почта: " + email);
        } else {
            nameText.setText("Не удалось загрузить имя");
            emailText.setText("Не удалось загрузить почту");
        }

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("user_id");
                editor.remove("role");
                editor.apply();

                Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AllDishes.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        });
    }
}
