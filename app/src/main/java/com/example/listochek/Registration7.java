package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import androidx.core.view.GestureDetectorCompat;
public class Registration7 extends AppCompatActivity {
    String age;
    String name;
    String height;
    String weight;
    String sex;
    String email;
    Button female;
    Button male;
    Button sexSave;
    UserModel userModel;
    private boolean isSexSelected = false;
    private GestureDetectorCompat gestureDetectorCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration7);

        female = findViewById(R.id.femaleSex);
        male = findViewById(R.id.maleSex);
        sexSave = findViewById(R.id.saveSex);
        name = getIntent().getExtras().getString("name");
        age = getIntent().getExtras().getString("age");
        height = getIntent().getExtras().getString("height");
        weight = getIntent().getExtras().getString("weight");
        email = FirebaseUtil.currentUserEmail();

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sex = "Ж";
                ((MaterialButton) female).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
                ((MaterialButton) male).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
                isSexSelected = true;

            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "М";
                ((MaterialButton) male).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
                ((MaterialButton) female).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
                isSexSelected = true;
            }
        });

        sexSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSexSelected) {
                    Toast.makeText(Registration7.this, "Пожалуйста, выберите образ жизни для продолжения", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Registration7.this, RegistrationFinish.class);
                    intent.putExtra("name", name);
                    intent.putExtra("age", age);
                    intent.putExtra("height", height);
                    intent.putExtra("weight", weight);
                    intent.putExtra("sex", sex);
                    startActivity(intent);
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
                        Intent intent = new Intent(Registration7.this, Registration6.class);
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