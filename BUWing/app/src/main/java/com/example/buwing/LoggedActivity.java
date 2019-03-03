package com.example.buwing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.name;
import static com.example.buwing.MainActivity.surname;

public class LoggedActivity extends AppCompatActivity {

    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        msg = findViewById(R.id.welcomeTextView);

        msg.setText("Witaj, " + name +  " " + surname + " " + login);
    }
}
