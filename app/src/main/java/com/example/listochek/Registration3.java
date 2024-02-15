package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registration3 extends AppCompatActivity {

    EditText usernameInput;
    Button usernameSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration3);

        usernameInput = findViewById(R.id.nameText);
        usernameSave = findViewById(R.id.saveName);
        usernameSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsername();
                Intent intent = new Intent(Registration3.this, Registration4.class);
                startActivity(intent);
            }
    void getUsername(){

    }
        });
    }
}