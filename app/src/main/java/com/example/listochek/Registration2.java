package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class Registration2 extends AppCompatActivity {

    private String userEmail;
    private EditText firstPass;
    private EditText secPass;
    private Button signUp;
    private FirebaseAuth mAuth;

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
                                        startActivity(new Intent(Registration2.this, Registration3.class));
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