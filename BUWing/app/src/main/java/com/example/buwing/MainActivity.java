package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    Button loginButton, registerButton;
    EditText loginTextView, passwordTextView;
    static String name;
    static String surname;
    static String login;
    static String password;

    @SuppressLint("StaticFieldLeak")
    private class AuthenticationTask extends AsyncTask<Void, Void, Boolean> {
        private StringBuilder stringBuilder = new StringBuilder();
        private JSONObject obj;
        private boolean loggedIn;

        AuthenticationTask(){}

        @Override
        protected Boolean doInBackground(Void... voids) {
            stringBuilder.append("http://students.mimuw.edu.pl/~kr394714/buwing/login.php?login=");
            stringBuilder.append(login);
            stringBuilder.append("&password=");
            stringBuilder.append(password);
            String loginURL = stringBuilder.toString();
            StringBuilder response = new StringBuilder();

            HttpURLConnection conn = null;

            try {
                URL url = new URL(loginURL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(false);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                int status = conn.getResponseCode();

                if (status != 200) {
                    throw new IOException("Post failed with error code " + status);
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                String result = response.toString();
                try {
                    obj = new JSONObject(result);
                    name = obj.get("name").toString();
                    surname = obj.get("surname").toString();
                    loggedIn = obj.get("loggedin").toString().equals("1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return loggedIn;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            checkLoginSuccess(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginTextView.getText().toString();
                password = passwordTextView.getText().toString();
                AuthenticationTask loginTask = new AuthenticationTask();
                loginTask.execute();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checkLoginSuccess(boolean loggedIn) {
        if (loggedIn) {
            Intent intent = new Intent(this, LoggedActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Błąd logowania", Toast.LENGTH_LONG).show();
        }
    }

}
