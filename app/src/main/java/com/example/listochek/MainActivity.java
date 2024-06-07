package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listochek.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private EditText emailET;
    private Button emailBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailET = findViewById(R.id.emailText);

        SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("user_id", null);
        String role = sharedPref.getString("role", null);
        if (userId != null && role != null) {
            if ("admin".equals(role)) {
                loadUserDataAndStartAdminActivity(userId);
            } else {
                Intent intent = new Intent(this, HomePage.class);
                startActivity(intent);
                finish();
            }
        } else if (userId != null && role == null) {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            finish();
        }

        Button button = findViewById(R.id.emailAddButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Intent intent = new Intent(MainActivity.this, Registration2.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else if (email.isEmpty()) {
                    emailET.setError("Пустые значения некорректны");
                } else {
                    emailET.setError("Введите корректный вид почты");
                }
            }
        });

        TextView toAuthorizationText = findViewById(R.id.toAuthorizationText);
        toAuthorizationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Authorization1.class);
                startActivity(intent);
            }
        });
    }

    private void loadUserDataAndStartAdminActivity(String userId) {
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        UserModel userModel = document.toObject(UserModel.class);
                        if (userModel != null) {
                            Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                            intent.putExtra("name", userModel.getName());
                            intent.putExtra("email", userModel.getEmail());
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
    }
}
