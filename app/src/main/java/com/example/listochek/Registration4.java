package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.core.view.GestureDetectorCompat;
public class Registration4 extends AppCompatActivity {
    String age;
    String name;
    EditText ageInput;
    Button ageSave;
    private GestureDetectorCompat gestureDetectorCompat;
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
        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Горизонтальный свайп
                    if (diffX > 0) {
                        // Свайп вправо
                        Intent intent = new Intent(Registration4.this, Registration3.class);
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