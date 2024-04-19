package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.core.view.GestureDetectorCompat;


public class Authorization3 extends AppCompatActivity {
    TextView heightView;
    EditText heightInput;
    Button heightButton;
    UserModel userModel;
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization3);

        heightInput = findViewById(R.id.heightText);
        heightButton = findViewById(R.id.saveHeight);
        heightView = findViewById(R.id.heightTextView);
        getUserHeight();
        heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserHeight();
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
                        Intent intent = new Intent(Authorization3.this, Authorization2.class);
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
    void setUserHeight(){
        String heightString = heightInput.getText().toString();
        if(heightString.isEmpty() || heightString.length()<3){
            heightInput.setError("Вы установили слишком низкий рост");
            return;
        }
        if(userModel!=null){
            Integer height = Integer.parseInt(heightString);
            userModel.setHeight(height);
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(Authorization3.this, AuthorizationFinish.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(Authorization3.this, "Ошибка авторизации: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    void getUserHeight() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null && userModel.getHeight() != null) {
                        heightInput.setText(String.valueOf(userModel.getHeight()));
                        heightView.setText("Ваш рост " + userModel.getHeight() + "?");
                    }
                }
            }
        });
    }

}