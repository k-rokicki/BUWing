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

    Button loguj, rejestruj;
    EditText nrIndeksu, haslo;
    static String imie;
    static String nazwisko;
    static String login;

    @SuppressLint("StaticFieldLeak")
    private class Authentication extends AsyncTask<Void, Void, Boolean> {
        private String nrIndeksu;
        private String haslo;
        private StringBuilder stringBuilder = new StringBuilder();
        private JSONObject obj;
        private boolean zalogowano;

        Authentication(String nrIndeksu, String haslo) {
            this.nrIndeksu = nrIndeksu;
            this.haslo = haslo;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            stringBuilder.append("http://students.mimuw.edu.pl/~kr394714/buwing/login.php?nrindeksu=");
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
                    imie = obj.get("imie").toString();
                    nazwisko = obj.get("nazwisko").toString();
                    login = nrIndeksu;
                    zalogowano = obj.get("zalogowano").toString().equals("1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return zalogowano;
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

        loguj = findViewById(R.id.loguj);
        rejestruj = findViewById(R.id.rejestruj);
        nrIndeksu = findViewById(R.id.nrIndeksu);
        haslo = findViewById(R.id.haslo);

        loguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authentication login = new Authentication(nrIndeksu.getText().toString(), haslo.getText().toString());
                login.execute();
            }
        });

        rejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checkLoginSuccess(boolean zalogowano) {
        if (zalogowano) {
            Intent intent = new Intent(this, LoggedActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Błąd logowania", Toast.LENGTH_LONG).show();
        }
    }

}
