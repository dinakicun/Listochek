package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Authorization1 extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView toRegistration;
    private TextView recoverTextView;
    private FirebaseAuth mAuth;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization1);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.editText);
        passwordEditText = findViewById(R.id.editText2);
        toRegistration = findViewById(R.id.toRegistrationText);
        recoverTextView = findViewById(R.id.recoverThePassword);

        findViewById(R.id.button).setOnClickListener(v -> loginUser());
        toRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Authorization1.this, MainActivity.class);
                startActivity(intent);
            }
        });

        recoverTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordRecoveryDialog();
            }
        });
    }

    void getUser(String email) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String userId = FirebaseUtil.currentUserId();
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        if ("admin".equals(userModel.getRole())) {
                            SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("user_id", userId);
                            editor.putString("role", "admin");
                            editor.apply();
                            Intent intent = new Intent(Authorization1.this, AdminMainActivity.class);
                            intent.putExtra("name", userModel.getName());
                            intent.putExtra("email", userModel.getEmail());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Authorization1.this, Authorization2.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(Authorization1.this, Registration3.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Authorization1.this, "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Authorization1.this, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                    getUser(email);

                } else {
                    Toast.makeText(Authorization1.this, "Ошибка авторизации: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showPasswordRecoveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Восстановление пароля");

        final EditText input = new EditText(this);
        input.setHint("Введите ваш email");
        builder.setView(input);

        builder.setPositiveButton("Отправить", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(Authorization1.this, "Пожалуйста, введите email", Toast.LENGTH_SHORT).show();
            } else {
                sendPasswordResetEmail(email);
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Authorization1.this, "Инструкция по восстановлению пароля отправлена на " + email, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Authorization1.this, "Ошибка отправки инструкции по восстановлению пароля: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
