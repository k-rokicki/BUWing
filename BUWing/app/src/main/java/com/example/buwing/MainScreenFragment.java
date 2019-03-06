package com.example.buwing;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MainScreenFragment extends BaseFragment {

    TextView fullnessInfoTextView;
    TextView openingHoursMsgTextView;
    TextView openingHoursTextView;
    int freeSeatsCount;
    int allSeatsCount;

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

    @SuppressLint("StaticFieldLeak")
    private class GetFullnessInfoTask extends AsyncTask<Void, Void, Void> {
        private JSONObject obj;

        GetFullnessInfoTask(){}

        @Override
        protected Void doInBackground(Void... voids) {
            String loginURL = "http://students.mimuw.edu.pl/~kr394714/buwing/fullness_info.php";
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
                    freeSeatsCount = Integer.parseInt(obj.get("freeSeatsCount").toString());
                    allSeatsCount = Integer.parseInt(obj.get("allSeatsCount").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Void result) {
            fullnessInfoTextView.setText(String.format("%d / %d", freeSeatsCount, allSeatsCount));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("ekran główny");

        fullnessInfoTextView = Objects.requireNonNull(getView()).findViewById(R.id.fullnessInfoTextView);
        openingHoursMsgTextView = Objects.requireNonNull(getView()).findViewById(R.id.openingHoursMsgTextView);
        openingHoursTextView = Objects.requireNonNull(getView()).findViewById(R.id.openingHoursTextView);

        SpannableString openingHoursMsgString = new SpannableString("godziny otwarcia");
        openingHoursMsgString.setSpan(new UnderlineSpan(), 0, openingHoursMsgString.length(), 0);
        openingHoursMsgTextView.setText(openingHoursMsgString);

        GetOpeningHoursTask getOpeningHoursTask = new GetOpeningHoursTask();
        getOpeningHoursTask.execute();

        GetFullnessInfoTask getFullnessInfoTask = new GetFullnessInfoTask();
        getFullnessInfoTask.execute();
    }

    @Override
    public void onBackPressed() {
    }
}