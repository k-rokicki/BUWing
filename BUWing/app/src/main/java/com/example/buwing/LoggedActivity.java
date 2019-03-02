package com.example.buwing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.buwing.MainActivity.imie;
import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.nazwisko;

public class LoggedActivity extends AppCompatActivity {

    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        msg = findViewById(R.id.welcomeMsg);

        msg.setText("Witaj, " + imie +  " " + nazwisko + " " + login);
    }
}
