package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class Registration7 extends AppCompatActivity {
    Spinner genderSpinner;
    String age;
    String name;
    String height;
    String weight;
    String sex;
    String email;
    Button sexSave;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration7);

        genderSpinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        sexSave = findViewById(R.id.saveSex);
        name = getIntent().getExtras().getString("name");
        age = getIntent().getExtras().getString("age");
        height = getIntent().getExtras().getString("height");
        weight = getIntent().getExtras().getString("weight");
        email = FirebaseUtil.currentUserEmail();

        sexSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sex = genderSpinner.getSelectedItem().toString();
                if(sex.equals("Женский")){
                    sex = "Ж";
                }
                else if(sex.equals("Мужской")){
                    sex = "М";
                }
                getUser();
            }
        });

    }
    void setUser(){
        Integer intAge = Integer.parseInt(age);
        Integer intHeight = Integer.parseInt(height);
        Integer intWeight = Integer.parseInt(weight);
        if (userModel == null) {
            userModel = new UserModel(email, name, intAge, intHeight, intWeight, sex);
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(Registration7.this,CalloriesTracker.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
            }
        });

    }
    void getUser(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    userModel =    task.getResult().toObject(UserModel.class);
                    if(userModel==null){
                        setUser();
                    }
                }
            }
        });
    }

}