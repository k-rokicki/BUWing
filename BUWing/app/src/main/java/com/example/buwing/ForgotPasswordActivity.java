package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.buwing.MainActivity.loginCredentials;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button confirmButton;
    EditText emailTextView;
    String email;

    @SuppressLint("StaticFieldLeak")
    private class ResetPasswordTask extends AsyncTask<Void, Void, Boolean> {
        private boolean resetLinkSent;

        ResetPasswordTask(){}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            String loginURL = "http://students.mimuw.edu.pl/~kr394714/buwing/reset_password.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("email", "UTF-8")
                        + "=" + URLEncoder.encode(email, "UTF-8");
                URL url = new URL(loginURL);

                conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(POSTdata);
                wr.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String result = response.toString();
                try {
                    obj = new JSONObject(result);
                    resetLinkSent = Boolean.parseBoolean(obj.get("resetLinkSent").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return resetLinkSent;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            checkResetSuccess(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("BUWing - resetowanie hasła");

        confirmButton = findViewById(R.id.confirmButton);
        emailTextView = findViewById(R.id.emailTextView);

        if (loginCredentials.exists()) {
            loginCredentials.delete();
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailTextView.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Wpisz adres email", Toast.LENGTH_LONG).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Nieprawidłowy adres email", Toast.LENGTH_LONG).show();
                } else {
                    ResetPasswordTask resetPasswordTask = new ResetPasswordTask();
                    resetPasswordTask.execute();
                }
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkResetSuccess (boolean resetLinkSent) {
        if (resetLinkSent) {
            Toast.makeText(getApplicationContext(),
                    "Ustaw nowe hasło klikając w link z maila", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Adres email nie jest przypisany do żadnego konta", Toast.LENGTH_LONG).show();
        }
    }
}
