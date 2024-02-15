package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;



public class Registration6 extends AppCompatActivity {
    EditText weightInput;
    Button weightSave;
    String age;
    String name;
    String height;
    String weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration6);


        weightSave = findViewById(R.id.saveWeight);
        name = getIntent().getExtras().getString("name");
        age = getIntent().getExtras().getString("age");
        height = getIntent().getExtras().getString("height");
        weightInput = findViewById(R.id.weightText);
        weightSave = findViewById(R.id.saveWeight);
        weightSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = weightInput.getText().toString();
                if (!weight.isEmpty()) {
                    try {
                        int weightValue = Integer.parseInt(weight);
                        if (weightValue >= 10 && weightValue <= 350) {
                            Intent intent = new Intent(Registration6.this, Registration7.class);
                            intent.putExtra("name", name);
                            intent.putExtra("age", age);
                            intent.putExtra("height", height);
                            intent.putExtra("weight", weight);
                            startActivity(intent);
                        } else {
                            weightInput.setError("Введите вес в диапазоне от 10 до 350 кг");
                        }
                    } catch (NumberFormatException e) {
                        weightInput.setError("Введите вес как число");
                    }
                } else {
                    weightInput.setError("Это поле не может быть пустым");
                }
            }
        });
    }
}