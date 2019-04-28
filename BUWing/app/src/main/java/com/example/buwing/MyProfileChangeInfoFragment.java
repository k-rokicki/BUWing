package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Objects;
import java.util.regex.Matcher;

import static com.example.buwing.MainActivity.loginCredentials;
import static com.example.buwing.MainActivity.password;
import static com.example.buwing.MainActivity.saveLoginCredentials;
import static com.example.buwing.RegisterActivity.notAllowedCharacterMessage;
import static com.example.buwing.RegisterActivity.notAllowedCharacterPattern;

public class MyProfileChangeInfoFragment extends BaseFragment {

    EditText nameEditText, surnameEditText, loginEditText, emailEditText;
    Button confirmButton;

    String newName, newSurname, newLogin, newEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile_change_info;
        title = "mój profil - zmiana danych";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameEditText = Objects.requireNonNull(getActivity()).findViewById(R.id.nameEditText);
        surnameEditText = Objects.requireNonNull(getActivity()).findViewById(R.id.surnameEditText);
        loginEditText = Objects.requireNonNull(getActivity()).findViewById(R.id.loginEditText);
        emailEditText = Objects.requireNonNull(getActivity()).findViewById(R.id.emailEditText);

        confirmButton = Objects.requireNonNull(getActivity()).findViewById(R.id.confirmButton);

        nameEditText.setText(MainActivity.name);
        surnameEditText.setText(MainActivity.surname);
        loginEditText.setText(MainActivity.login);
        emailEditText.setText(MainActivity.email);

        confirmButton.setOnClickListener(v -> {
            newName = nameEditText.getText().toString();
            newSurname = surnameEditText.getText().toString();
            newLogin = loginEditText.getText().toString();
            newEmail = emailEditText.getText().toString();

            Matcher nameMatcher = notAllowedCharacterPattern.matcher(newName);
            Matcher surnameMatcher = notAllowedCharacterPattern.matcher(newSurname);
            Matcher loginMatcher = notAllowedCharacterPattern.matcher(newLogin);

            if (newName.isEmpty() || newSurname.isEmpty() || newLogin.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Uzupełnij wszystkie pola", Toast.LENGTH_LONG).show();
            } else if (nameMatcher.find()) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        notAllowedCharacterMessage + "imię", Toast.LENGTH_LONG).show();
            } else if (surnameMatcher.find()) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        notAllowedCharacterMessage + "nazwisko", Toast.LENGTH_LONG).show();
            } else if (loginMatcher.find()) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        notAllowedCharacterMessage + "login", Toast.LENGTH_LONG).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Nieprawidłowy adres email", Toast.LENGTH_LONG).show();
            } else {
                UpdateInfoTask updateInfoTask = new UpdateInfoTask();
                updateInfoTask.execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new MyProfileChangeFragment();
        FragmentTransaction ft =
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations
                                (android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateInfoTask extends AsyncTask<Void, Void, Boolean> {
        private boolean updated;

        UpdateInfoTask(){}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            String updateURL = Constants.webserviceURL + "update_profile_info.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password, "UTF-8")
                        + "&" + URLEncoder.encode("newName", "UTF-8")
                        + "=" + URLEncoder.encode(newName, "UTF-8")
                        + "&" + URLEncoder.encode("newSurname", "UTF-8")
                        + "=" + URLEncoder.encode(newSurname, "UTF-8")
                        + "&" + URLEncoder.encode("newLogin", "UTF-8")
                        + "=" + URLEncoder.encode(newLogin, "UTF-8")
                        + "&" + URLEncoder.encode("newEmail", "UTF-8")
                        + "=" + URLEncoder.encode(newEmail, "UTF-8");
                URL url = new URL(updateURL);

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
                    updated = Boolean.parseBoolean(obj.get("updated").toString());
                    newName = obj.getString("name");
                    newSurname = obj.getString("surname");
                    newLogin = obj.getString("login");
                    newEmail = obj.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return updated;
        }

        @Override
        protected void onPostExecute(Boolean updated) {
            checkUpdateSuccess(updated);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkUpdateSuccess(boolean updated) {
        if (updated) {
            boolean logout = (!newEmail.equals(MainActivity.email));

            MainActivity.name = newName;
            MainActivity.surname = newSurname;
            MainActivity.login = newLogin;

            Objects.requireNonNull(getActivity()).runOnUiThread(LoggedInActivity::updateUserInfo);

            if (!logout) {
                try {
                    saveLoginCredentials(Objects.requireNonNull(getActivity()).getBaseContext());
                } catch (IOException e) {
                    if (loginCredentials.exists()) {
                        loginCredentials.delete();
                    }
                    e.printStackTrace();
                }
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Pomyślnie zmieniono dane", Toast.LENGTH_LONG).show();
                Fragment fragment = new MyProfileFragment();
                FragmentTransaction ft =
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                                beginTransaction().
                                setCustomAnimations
                                        (android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            } else {
                if (loginCredentials.exists()) {
                    loginCredentials.delete();
                }
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Pomyślnie zmieniono dane. Aktywuj konto klikając w link z maila", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        }
    }

}