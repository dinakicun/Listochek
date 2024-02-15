package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class Authorization3 extends AppCompatActivity {
    TextView heightView;
    EditText heightInput;
    Button heightButton;
    UserModel userModel;
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
                    Intent intent = new Intent(Authorization3.this, CalloriesTracker.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(Authorization3.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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