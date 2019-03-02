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

public class RegisterActivity extends AppCompatActivity {

    Button zarejestruj;
    EditText noweImie, noweNazwisko, nowyNrIndeksu, noweHaslo;

    @SuppressLint("StaticFieldLeak")
    private class Registration extends AsyncTask<Void, Void, Boolean> {
        private String imie;
        private String nazwisko;
        private String nrIndeksu;
        private String haslo;
        private StringBuilder stringBuilder = new StringBuilder();
        private JSONObject obj;
        private boolean zarejestrowano;

        Registration(String imie, String nazwisko, String nrIndeksu, String haslo) {
            this.imie = imie;
            this.nazwisko = nazwisko;
            this.nrIndeksu = nrIndeksu;
            this.haslo = haslo;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            stringBuilder.append("http://students.mimuw.edu.pl/~kr394714/buwing/register.php?imie=");
            stringBuilder.append(imie);
            stringBuilder.append("&nazwisko=");
            stringBuilder.append(nazwisko);
            stringBuilder.append("&nrindeksu=");
            stringBuilder.append(nrIndeksu);
            stringBuilder.append("&haslo=");
            stringBuilder.append(haslo);
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
                    zarejestrowano = obj.get("zarejestrowano").toString().equals("1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return zarejestrowano;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            checkRegisterSuccess(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        zarejestruj = findViewById(R.id.zarejestruj);
        noweImie = findViewById(R.id.noweImie);
        noweNazwisko = findViewById(R.id.noweNazwisko);
        nowyNrIndeksu = findViewById(R.id.nowyNrIndeksu);
        noweHaslo = findViewById(R.id.noweHaslo);

        zarejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String _imie = noweImie.getText().toString();
                final String _nazwisko = noweNazwisko.getText().toString();
                final String _nrIndeksu = nowyNrIndeksu.getText().toString();
                final String _haslo = noweHaslo.getText().toString();

                if (_imie.isEmpty() || _nazwisko.isEmpty() || _nrIndeksu.isEmpty() || _haslo.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_LONG).show();
                } else {
                    RegisterActivity.Registration registration = new RegisterActivity.Registration
                            (_imie, _nazwisko, _nrIndeksu, _haslo);
                    registration.execute();
                }
            }
        });
    }

    public void checkRegisterSuccess(boolean zarejestrowano) {
        if (zarejestrowano) {
            Toast.makeText(getApplicationContext(), "Pomyślnie zarejestrowano. Zaloguj się", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        }
    }
}
