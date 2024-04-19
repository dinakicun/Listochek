package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.core.view.GestureDetectorCompat;

public class Registration2 extends AppCompatActivity {

    private String userEmail;
    private EditText firstPass;
    private EditText secPass;
    private Button signUp;
    private FirebaseAuth mAuth;
    private GestureDetectorCompat gestureDetectorCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        userEmail = getIntent().getExtras().getString("email");

        firstPass = findViewById(R.id.inputPasText);

        secPass = findViewById(R.id.inputPasText2);

        signUp = findViewById(R.id.signUpButton);
        mAuth = FirebaseAuth.getInstance();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createUser();

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
                        Intent intent = new Intent(Registration2.this, MainActivity.class);
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
    private void createUser(){
        String email = userEmail;
        String pass = firstPass.getText().toString();
        String pass2 = secPass.getText().toString();
        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (pass.equals(pass2)) {
                if (!pass.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Registration2.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Registration2.this, Registration3.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Registration2.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Registration2.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    firstPass.setError("Введите все значения");
                }
            } else {
                Toast.makeText(Registration2.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(Registration2.this, "Проблема в почте", Toast.LENGTH_SHORT).show();
        }
    }
}