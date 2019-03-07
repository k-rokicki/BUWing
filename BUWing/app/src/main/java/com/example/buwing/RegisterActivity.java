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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.buwing.MainActivity.loginCredentials;
import static com.example.buwing.MainActivity.saveLoginCredentials;

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;
    EditText nameTextView, surnameTextView, loginTextView, passwordTextView;
    String name, surname, login, password;

    String notAllowedCharacterPatternString = "^.*['\"();].*$";
    Pattern notAllowedCharacterPattern = Pattern.compile(notAllowedCharacterPatternString);
    String notAllowedCharacterMessage = "Niedozwolony znak w polu: ";

    @SuppressLint("StaticFieldLeak")
    private class RegistrationTask extends AsyncTask<Void, Void, Integer> {
        private StringBuilder stringBuilder = new StringBuilder();
        private JSONObject obj;
        private int registered;

        RegistrationTask(){}

        @Override
        protected Integer doInBackground(Void... voids) {
            stringBuilder.append("http://students.mimuw.edu.pl/~kr394714/buwing/register.php?name=");
            stringBuilder.append(name);
            stringBuilder.append("&surname=");
            stringBuilder.append(surname);
            stringBuilder.append("&login=");
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
                    registered = Integer.parseInt(obj.get("registered").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return registered;
        }

        @Override
        protected void onPostExecute(Integer result) {
            checkRegisterSuccess(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.registerButton);
        nameTextView = findViewById(R.id.nameTextView);
        surnameTextView = findViewById(R.id.surnameTextView);
        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameTextView.getText().toString();
                surname = surnameTextView.getText().toString();
                login = loginTextView.getText().toString();
                password = passwordTextView.getText().toString();

                Matcher nameMatcher = notAllowedCharacterPattern.matcher(name);
                Matcher surnameMatcher = notAllowedCharacterPattern.matcher(surname);
                Matcher loginMatcher = notAllowedCharacterPattern.matcher(login);

                if (name.isEmpty() || surname.isEmpty() || login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_LONG).show();
                } else if (nameMatcher.find()) {
                    Toast.makeText(getApplicationContext(), notAllowedCharacterMessage + "imię", Toast.LENGTH_LONG).show();
                } else if (surnameMatcher.find()) {
                    Toast.makeText(getApplicationContext(), notAllowedCharacterMessage + "nazwisko", Toast.LENGTH_LONG).show();
                } else if (loginMatcher.find()) {
                    Toast.makeText(getApplicationContext(), notAllowedCharacterMessage + "login", Toast.LENGTH_LONG).show();
                } else {
                    RegistrationTask registrationTask = new RegistrationTask();
                    registrationTask.execute();
                }
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkRegisterSuccess(int registered) {
        if (registered == 1) {
            MainActivity.name = name;
            MainActivity.surname = surname;
            MainActivity.login = login;
            MainActivity.password = password;
            try {
                saveLoginCredentials(getBaseContext());
            } catch (IOException e) {
                if (loginCredentials.exists()) {
                    loginCredentials.delete();
                }
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Pomyślnie zarejestrowano", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoggedInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (registered == 0){
            Toast.makeText(getApplicationContext(), "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        } else if (registered == -1) {
            Toast.makeText(getApplicationContext(), "Login już zajęty", Toast.LENGTH_LONG).show();
        }
    }
}
