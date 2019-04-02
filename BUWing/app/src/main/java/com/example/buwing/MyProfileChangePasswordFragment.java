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
import java.util.Objects;
import java.util.regex.Matcher;

import static com.example.buwing.MainActivity.loginCredentials;
import static com.example.buwing.RegisterActivity.minPasswordLength;
import static com.example.buwing.RegisterActivity.specialCharacters;
import static com.example.buwing.RegisterActivity.uppercaseLetterPattern;
import static com.example.buwing.RegisterActivity.lowercaseLetterPattern;
import static com.example.buwing.RegisterActivity.digitPattern;
import static com.example.buwing.RegisterActivity.specialCharacterPattern;

public class MyProfileChangePasswordFragment extends BaseFragment {

    TextView oldPasswordTextView, newPasswordTextView, newPasswordRepeatTextView;
    Button confirmButton;

    String oldPassword, newPassword, newPasswordRepeat;
    int attempts = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile_change_password;
        title = "mój profil - zmiana hasła";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        oldPasswordTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.oldPasswordTextView);
        newPasswordTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.newPasswordTextView);
        newPasswordRepeatTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.newPasswordRepeatTextView);

        confirmButton = Objects.requireNonNull(getActivity()).findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                oldPassword = oldPasswordTextView.getText().toString();
                newPassword = newPasswordTextView.getText().toString();
                newPasswordRepeat = newPasswordRepeatTextView.getText().toString();

                Matcher uppercaseLetterMatcher = uppercaseLetterPattern.matcher(newPassword);
                Matcher lowercaseLetterMatcher = lowercaseLetterPattern.matcher(newPassword);
                Matcher digitMatcher = digitPattern.matcher(newPassword);
                Matcher specialCharacterMatcher = specialCharacterPattern.matcher(newPassword);

                if (oldPassword.isEmpty() || newPassword.isEmpty() || newPasswordRepeat.isEmpty()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            "Uzupełnij wszystkie pola", Toast.LENGTH_LONG).show();
                } else if (!newPassword.equals(newPasswordRepeat)) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                             "Hasła nie są takie same", Toast.LENGTH_LONG).show();
                } else if (!uppercaseLetterMatcher.find()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            "Hasło musi zawierać wielką literę", Toast.LENGTH_LONG).show();
                } else if (!lowercaseLetterMatcher.find()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            "Hasło musi zawierać małą literę", Toast.LENGTH_LONG).show();
                } else if (!digitMatcher.find()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            "Hasło musi zawierać cyfrę", Toast.LENGTH_LONG).show();
                } else if (!specialCharacterMatcher.find()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            String.format("Hasło musi zawierać znak specjalny: %s", specialCharacters), Toast.LENGTH_LONG).show();
                } else if (newPassword.length() < minPasswordLength) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            String.format("Hasło musi mieć co najmniej %d znaków", minPasswordLength), Toast.LENGTH_LONG).show();
                } else {
                    UpdatePasswordTask updatePasswordTask = new UpdatePasswordTask();
                    updatePasswordTask.execute();
                }
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
    private class UpdatePasswordTask extends AsyncTask<Void, Void, Integer> {
        private int updated;

        UpdatePasswordTask(){}

        @Override
        protected Integer doInBackground(Void... voids) {
            JSONObject obj;
            String updateURL = Constants.webserviceURL + "update_profile_password.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("oldPassword", "UTF-8")
                        + "=" + URLEncoder.encode(oldPassword, "UTF-8")
                        + "&" + URLEncoder.encode("newPassword", "UTF-8")
                        + "=" + URLEncoder.encode(newPassword, "UTF-8");
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
                    updated = Integer.parseInt(obj.get("updated").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return updated;
        }

        @Override
        protected void onPostExecute(Integer updated) {
            checkUpdateSuccess(updated);
        }
    }

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkUpdateSuccess(int updated) {
        if (updated == -1) {
            attempts++;
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    String.format("Błędne hasło. Pozostało prób: %d", 3 - attempts), Toast.LENGTH_LONG).show();
            if (attempts >= 3) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                if (loginCredentials.exists()) {
                    loginCredentials.delete();
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else if (updated == 0) {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        } else if (updated == 1) {
            if (loginCredentials.exists()) {
                loginCredentials.delete();
            }
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Aby potwierdzić zmianę hasła, kliknij w link z maila", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}