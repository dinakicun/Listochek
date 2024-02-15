package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registration4 extends AppCompatActivity {
    String age;
    String name;
    EditText ageInput;
    Button ageSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration4);

        name = getIntent().getExtras().getString("name");
        ageInput = findViewById(R.id.ageText);
        ageSave = findViewById(R.id.saveAge);
        ageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age = ageInput.getText().toString();
                try {
                    int ageInt = Integer.parseInt(age);
                    if(ageInt < 0 || ageInt > 120) { // Проверяем, что возраст находится в допустимом диапазоне
                        ageInput.setError("Введите возраст в диапазоне от 0 до 120 лет");
                        return;
                    }

                    Intent intent = new Intent(Registration4.this, Registration5.class);
                    intent.putExtra("name", name);
                    intent.putExtra("age", age);
                    startActivity(intent);
                } catch(NumberFormatException e) {
                    ageInput.setError("Введите корректный возраст (только числа)");
                }
            }
        });
    }
}