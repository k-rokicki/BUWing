package com.example.buwing;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.name;
import static com.example.buwing.MainActivity.surname;

public class LoggedActivity extends AppCompatActivity {

    TextView msg;
    TextView openingHoursTextView;

    @SuppressLint("StaticFieldLeak")
    private class GetOpeningHoursTask extends AsyncTask<Void, Void, Void> {

        String openingHoursString;

        GetOpeningHoursTask() {
            openingHoursString = null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String loginURL = "http://students.mimuw.edu.pl/~kr394714/buwing/opening_hours.php";

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
                    openingHoursString = in.readLine();
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            openingHoursTextView.setText(openingHoursString);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        msg = findViewById(R.id.welcomeTextView);
        openingHoursTextView = findViewById(R.id.openingHoursTextView);

        GetOpeningHoursTask getOpeningHoursTask = new GetOpeningHoursTask();
        getOpeningHoursTask.execute();

        msg.setText("Witaj, " + name +  " " + surname + " " + login);
    }
}
