package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.core.view.GestureDetectorCompat;


public class Registration6 extends AppCompatActivity {
    EditText weightInput;
    Button weightSave;
    String age;
    String name;
    String height;
    String weight;
    private GestureDetectorCompat gestureDetectorCompat;

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
        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Горизонтальный свайп
                    if (diffX > 0) {
                        // Свайп вправо
                        Intent intent = new Intent(Registration6.this, Registration5.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}