package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Authorization1 extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth; // Firebase authentication reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization1);

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        emailEditText = findViewById(R.id.editText);
        passwordEditText = findViewById(R.id.editText2);

        findViewById(R.id.button).setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Authorization1.this, "Email/Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate with Firebase, and if email and password are correct, Firebase will return an AuthResult object
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(Authorization1.this, "Authentication Successful.", Toast.LENGTH_SHORT).show();
                    // Here you can start a new Activity after successful login
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(Authorization1.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}