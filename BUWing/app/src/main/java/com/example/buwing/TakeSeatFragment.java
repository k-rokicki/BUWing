package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import static com.example.buwing.MainActivity.password;
import static com.example.buwing.MainActivity.saveLoginCredentials;
import static com.example.buwing.MainActivity.seatTaken;
import static com.example.buwing.MainActivity.takenSeatFloor;
import static com.example.buwing.MainActivity.takenSeatId;
import static java.util.Objects.requireNonNull;

public class TakeSeatFragment extends BaseFragment {

    @SuppressLint("StaticFieldLeak")
    static TextView floorTextView;
    @SuppressLint("StaticFieldLeak")
    static TextView tableNumberTextView;
    Button releaseTableButton;

    static final String defaultTextViewString = "-";
    static String floorTextViewString = defaultTextViewString;
    static String tableNumberTextViewString = defaultTextViewString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        MainScreenFragment.CheckSeatTakenTask checkSeatTakenTask = new MainScreenFragment.CheckSeatTakenTask();
        checkSeatTakenTask.execute();
        if (!seatTaken) {
            _layout = R.layout.fragment_take_seat;
        } else {
            _layout = R.layout.fragment_seat_taken;
        }
        title = "zajmij miejsce";
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!seatTaken)
            return inflater.inflate(R.layout.fragment_take_seat, container, false);
        else
            return inflater.inflate(R.layout.fragment_seat_taken, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireNonNull(getActivity()).setTitle("ekran główny");

        if (!seatTaken) {

        } else {
            floorTextView = requireNonNull(getView()).findViewById(R.id.floorTextView);
            tableNumberTextView = requireNonNull(getView()).findViewById(R.id.tableNumberTextView);
            releaseTableButton = requireNonNull(getView()).findViewById(R.id.releaseTableButton);

            floorTextViewString = String.valueOf(takenSeatFloor);
            tableNumberTextViewString = String.valueOf(takenSeatId);

            floorTextView.setText(floorTextViewString);
            tableNumberTextView.setText(tableNumberTextViewString);

            releaseTableButton.setOnClickListener(v -> {
                ReleaseTableTask releaseTableTask = new ReleaseTableTask();
                releaseTableTask.execute();
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ReleaseTableTask extends AsyncTask<Void, Void, Boolean> {
        private boolean released = false;

        ReleaseTableTask() {}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            String updateURL = Constants.webserviceURL + "release_table.php";
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
                    released = Boolean.parseBoolean(obj.get("released").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return released;
        }

        @Override
        protected void onPostExecute(Boolean released) {
            checkReleaseSuccess(released);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkReleaseSuccess(boolean released) {
        if (released) {
            seatTaken = false;
            takenSeatId = -1;
            takenSeatFloor = -1;

            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Pomyślnie zwolniono miejsce", Toast.LENGTH_LONG).show();

            FragmentManager fragmentManager = getFragmentManager();
            requireNonNull(fragmentManager).popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            LoggedInActivity.navigationView.getMenu().getItem(0).setChecked(true);

            Fragment fragment = new MainScreenFragment();
            FragmentTransaction ft =
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                            beginTransaction().
                            setCustomAnimations
                                    (android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        } else {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        }
    }

}