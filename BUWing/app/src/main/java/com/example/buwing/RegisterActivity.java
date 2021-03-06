package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;
    EditText nameTextView, surnameTextView, loginTextView, emailTextView, passwordTextView, passwordRepeatTextView;
    String name, surname, login, email, password;
    TextView passwordConstraintsTextView;

    static String specialCharacters = "@$!%*?&,.;";

    static String notAllowedCharacterPatternString = "^.*['\"();].*$";
    static Pattern notAllowedCharacterPattern = Pattern.compile(notAllowedCharacterPatternString);
    static String notAllowedCharacterMessage = "Niedozwolony znak w polu: ";
    static Pattern uppercaseLetterPattern = Pattern.compile("[A-Z]+");
    static Pattern lowercaseLetterPattern = Pattern.compile("[a-z]+");
    static Pattern digitPattern = Pattern.compile("[0-9]+");
    static Pattern specialCharacterPattern = Pattern.compile(String.format("[%s]", specialCharacters));

    static final int minPasswordLength = 8;

    @SuppressLint("StaticFieldLeak")
    private class RegistrationTask extends AsyncTask<Void, Void, Integer> {
        private int registered;

        RegistrationTask(){}

        @Override
        protected Integer doInBackground(Void... voids) {
            JSONObject obj;
            String loginURL = Constants.webserviceURL + "register.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("name", "UTF-8")
                        + "=" + URLEncoder.encode(name, "UTF-8")
                        + "&" + URLEncoder.encode("surname", "UTF-8")
                        + "=" + URLEncoder.encode(surname, "UTF-8")
                        + "&" + URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(login, "UTF-8")
                        + "&" + URLEncoder.encode("email", "UTF-8")
                        + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password, "UTF-8");
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
        setTitle("BUWing - załóż konto");

        registerButton = findViewById(R.id.registerButton);
        nameTextView = findViewById(R.id.nameTextView);
        surnameTextView = findViewById(R.id.surnameTextView);
        loginTextView = findViewById(R.id.loginTextView);
        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        passwordRepeatTextView = findViewById(R.id.passwordRepeatTextView);
        passwordConstraintsTextView = findViewById(R.id.passwordConstraintsTextView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        if (screenHeight < 1500) {
            passwordConstraintsTextView.setVisibility(View.INVISIBLE);
        }

        if (loginCredentials.exists()) {
            loginCredentials.delete();
        }

        registerButton.setOnClickListener(v -> {
            name = nameTextView.getText().toString();
            surname = surnameTextView.getText().toString();
            login = loginTextView.getText().toString();
            email = emailTextView.getText().toString();
            password = passwordTextView.getText().toString();
            String passwordRepeat = passwordRepeatTextView.getText().toString();

            Matcher nameMatcher = notAllowedCharacterPattern.matcher(name);
            Matcher surnameMatcher = notAllowedCharacterPattern.matcher(surname);
            Matcher loginMatcher = notAllowedCharacterPattern.matcher(login);
            Matcher uppercaseLetterMatcher = uppercaseLetterPattern.matcher(password);
            Matcher lowercaseLetterMatcher = lowercaseLetterPattern.matcher(password);
            Matcher digitMatcher = digitPattern.matcher(password);
            Matcher specialCharacterMatcher = specialCharacterPattern.matcher(password);

            if (name.isEmpty() || surname.isEmpty() || login.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_LONG).show();
            } else if (login.length() > 30) {
                Toast.makeText(getApplicationContext(), "Login może zawierać do 30 znaków", Toast.LENGTH_LONG).show();
            } else if (nameMatcher.find()) {
                Toast.makeText(getApplicationContext(), notAllowedCharacterMessage + "imię", Toast.LENGTH_LONG).show();
            } else if (surnameMatcher.find()) {
                Toast.makeText(getApplicationContext(), notAllowedCharacterMessage + "nazwisko", Toast.LENGTH_LONG).show();
            } else if (loginMatcher.find()) {
                Toast.makeText(getApplicationContext(), notAllowedCharacterMessage + "login", Toast.LENGTH_LONG).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getApplicationContext(), "Nieprawidłowy adres email", Toast.LENGTH_LONG).show();
            } else if (!password.equals(passwordRepeat)) {
                Toast.makeText(getApplicationContext(), "Hasła nie są takie same", Toast.LENGTH_LONG).show();
            } else if (!uppercaseLetterMatcher.find()) {
                Toast.makeText(getApplicationContext(), "Hasło musi zawierać wielką literę", Toast.LENGTH_LONG).show();
            } else if (!lowercaseLetterMatcher.find()) {
                Toast.makeText(getApplicationContext(), "Hasło musi zawierać małą literę", Toast.LENGTH_LONG).show();
            } else if (!digitMatcher.find()) {
                Toast.makeText(getApplicationContext(), "Hasło musi zawierać cyfrę", Toast.LENGTH_LONG).show();
            } else if (!specialCharacterMatcher.find()) {
                Toast.makeText(getApplicationContext(), String.format("Hasło musi zawierać znak specjalny: %s", specialCharacters), Toast.LENGTH_LONG).show();
            } else if (password.length() < minPasswordLength) {
                Toast.makeText(getApplicationContext(), String.format("Hasło musi mieć co najmniej %d znaków", minPasswordLength), Toast.LENGTH_LONG).show();
            } else {
                RegistrationTask registrationTask = new RegistrationTask();
                registrationTask.execute();
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkRegisterSuccess(int registered) {
        if (registered == 1) {
            Toast.makeText(getApplicationContext(),
                    "Pomyślnie zarejestrowano. Aktywuj konto klikając w link z maila", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (registered == 0){
            Toast.makeText(getApplicationContext(), "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        } else if (registered == -1) {
            Toast.makeText(getApplicationContext(), "Login lub email już zajęty", Toast.LENGTH_LONG).show();
        }
    }
}
