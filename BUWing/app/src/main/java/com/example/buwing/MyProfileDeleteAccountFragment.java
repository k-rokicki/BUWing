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

import static com.example.buwing.MainActivity.loginCredentials;

public class MyProfileDeleteAccountFragment extends BaseFragment {

    TextView passwordTextView, repeatPasswordTextView;
    Button confirmButton;

    String password, repeatPassword;
    int attempts = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile_delete_account;
        title = "mój profil - usuwanie konta";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        passwordTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.passwordTextView);
        repeatPasswordTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.passwordRepeatTextView);

        confirmButton = Objects.requireNonNull(getActivity()).findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordTextView.getText().toString();
                repeatPassword = repeatPasswordTextView.getText().toString();

                if (password.isEmpty() || repeatPassword.isEmpty()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            "Uzupełnij wszystkie pola", Toast.LENGTH_LONG).show();
                } else if (!password.equals(repeatPassword)) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                             "Hasła nie są takie same", Toast.LENGTH_LONG).show();
                } else {
                    DeleteAccountTask deleteAccountTask = new DeleteAccountTask();
                    deleteAccountTask.execute();
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
    private class DeleteAccountTask extends AsyncTask<Void, Void, Integer> {
        private int deleted;

        DeleteAccountTask(){}

        @Override
        protected Integer doInBackground(Void... voids) {
            JSONObject obj;
            String updateURL = Constants.webserviceURL + "delete_account.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password, "UTF-8");
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
                    deleted = Integer.parseInt(obj.get("deleted").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return deleted;
        }

        @Override
        protected void onPostExecute(Integer deleted) {
            checkUpdateSuccess(deleted);
        }
    }

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkUpdateSuccess(int deleted) {
        if (deleted == -1) {
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
        } else if (deleted == 0) {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        } else if (deleted == 1) {
            if (loginCredentials.exists()) {
                loginCredentials.delete();
            }
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Aby potwierdzić usunięcie konta, kliknij w link z maila", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}