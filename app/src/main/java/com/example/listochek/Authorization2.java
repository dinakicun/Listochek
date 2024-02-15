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

public class Authorization2 extends AppCompatActivity {

    TextView weightView;
    EditText weigthInput;
    Button weightButton;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization2);

        weigthInput = findViewById(R.id.weightText);
        weightButton = findViewById(R.id.weightSave);
        weightView = findViewById(R.id.weightTextView);
        getUserWeight();
        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserWeight();
            }

        });
    }
    void setUserWeight(){
       String weightString = weigthInput.getText().toString();
       if(weightString.isEmpty() || weightString.length()<2){
           weigthInput.setError("Вы установили слишком низкий вес");
           return;
       }
       if(userModel!=null){
           Integer weight = Integer.parseInt(weightString);
           userModel.setWeight(weight);
       }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   Intent intent = new Intent(Authorization2.this, Authorization3.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   startActivity(intent);

               }
               else{
                   Toast.makeText(Authorization2.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
               }
           }
       });
    }
    void getUserWeight(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                 userModel = task.getResult().toObject(UserModel.class);
                 if(userModel != null && userModel.getWeight() != null){
                     weigthInput.setText(String.valueOf(userModel.getWeight()));
                     weightView.setText("Ваш вес " + userModel.getWeight() + "?");
                 }
                }
            }
        });
    }
}