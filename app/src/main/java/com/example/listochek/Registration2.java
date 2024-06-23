package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseUser;
import androidx.core.view.GestureDetectorCompat;

public class Registration2 extends AppCompatActivity {

    private String userEmail;
    private EditText firstPass;
    private EditText secPass;
    private Button signUp;
    private FirebaseAuth mAuth;
    private GestureDetectorCompat gestureDetectorCompat;
    private Handler handler;
    private Runnable verificationCheck;

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
                    // Horizontal swipe
                    if (diffX > 0) {
                        // Swipe right
                        Intent intent = new Intent(Registration2.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        handler = new Handler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void createUser() {
        String email = userEmail;
        String pass = firstPass.getText().toString();
        String pass2 = secPass.getText().toString();
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (pass.equals(pass2)) {
                if (!pass.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            sendEmailVerification(user);
                                        }
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
        } else {
            Toast.makeText(Registration2.this, "Проблема в почте", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Registration2.this, "Проверьте почту для верификации", Toast.LENGTH_SHORT).show();
                    checkEmailVerification(user);
                } else {
                    Toast.makeText(Registration2.this, "Не удалось отправить верификационное письмо", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkEmailVerification(FirebaseUser user) {
        verificationCheck = new Runnable() {
            @Override
            public void run() {
                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (user.isEmailVerified()) {
                            Toast.makeText(Registration2.this, "Почта верифицирована", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Registration2.this, Registration3.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showVerificationFailedDialog(user);
                        }
                    }
                });
            }
        };
        handler.postDelayed(verificationCheck, 30000); // 30 seconds delay
    }

    private void showVerificationFailedDialog(FirebaseUser user) {
        new AlertDialog.Builder(this)
                .setTitle("Почта не верифицирована")
                .setMessage("Ваша почта не была верифицирована в течение 30 секунд. Ваш аккаунт будет удален.")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Registration2.this, "Аккаунт удален", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Registration2.this, "Не удалось удалить аккаунт", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setCancelable(false)
                .show();
    }
}
