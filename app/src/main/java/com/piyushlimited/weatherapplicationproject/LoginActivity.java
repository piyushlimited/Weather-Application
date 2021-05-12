package com.piyushlimited.weatherapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        EditText username = (EditText) findViewById(R.id.usernameInput);
        EditText password = (EditText) findViewById(R.id.passwordInput);
        Button login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_username = username.getText().toString().trim();
                String str_password = password.getText().toString().trim();

                if(str_username.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter your username.",Toast.LENGTH_SHORT).show();
                }
                else if(str_password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter your password",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(str_username.equals("piyush") && str_password.equals("12345678")){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Incorrect Username or Password.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}