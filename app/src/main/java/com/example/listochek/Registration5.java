package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registration5 extends AppCompatActivity {

    String age;
    String name;
    String height;
    EditText heightInput;
    Button heightSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration5);

        heightSave = findViewById(R.id.saveHeight);
        name = getIntent().getExtras().getString("name");
        age = getIntent().getExtras().getString("age");
        heightInput = findViewById(R.id.heightText);
        heightSave = findViewById(R.id.saveHeight);
        heightSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height = heightInput.getText().toString();
                if (!height.isEmpty()) {
                    try {
                        int heightValue = Integer.parseInt(height);
                        if (heightValue >= 50 && heightValue <= 250) {
                            Intent intent = new Intent(Registration5.this, Registration6.class);
                            intent.putExtra("name", name);
                            intent.putExtra("age", age);
                            intent.putExtra("height", height);
                            startActivity(intent);
                        } else {
                            heightInput.setError("Введите рост в диапазоне от 50 до 250 см");
                        }
                    } catch (NumberFormatException e) {
                        heightInput.setError("Введите рост как число");
                    }
                } else {
                    heightInput.setError("Это поле не может быть пустым");
                }
            }
        });
    }
}