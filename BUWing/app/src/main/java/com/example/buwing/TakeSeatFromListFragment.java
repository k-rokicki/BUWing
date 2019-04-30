package com.example.buwing;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.password;
import static com.example.buwing.MainActivity.seatTaken;
import static com.example.buwing.MainActivity.takenSeatFloor;
import static com.example.buwing.MainActivity.takenSeatId;
import static com.example.buwing.MainScreenFragment.availableFloors;
import static com.example.buwing.MainScreenFragment.availableTablesAtFloors;
import static com.example.buwing.MainScreenFragment.isLibraryOpen;
import static java.util.Objects.requireNonNull;

public class TakeSeatFromListFragment extends BaseFragment {

    Spinner floorSpinner;
    Spinner tableNumberSpinner;

    ArrayAdapter<Integer> availableFloorsAdapter;
    ArrayAdapter<Integer> availableTablesAdapter;

    Button takeSeatButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        MainScreenFragment.CheckSeatTakenTask checkSeatTakenTask = new MainScreenFragment.CheckSeatTakenTask();
        checkSeatTakenTask.execute();
        _layout = R.layout.fragment_take_seat_from_list;
        title = "zajmij miejsce";
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_take_seat_from_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireNonNull(getActivity()).setTitle("zajmij miejsce");

        floorSpinner = requireNonNull(getView()).findViewById(R.id.floorSpinner);
        tableNumberSpinner = requireNonNull(getView()).findViewById(R.id.tableNumberSpinner);
        MainScreenFragment.GetAvailableTablesTask getAvailableTablesTask = new MainScreenFragment.GetAvailableTablesTask();
        getAvailableTablesTask.execute();

        floorSpinner = requireNonNull(getView()).findViewById(R.id.floorSpinner);
        tableNumberSpinner = requireNonNull(getView()).findViewById(R.id.tableNumberSpinner);

        availableFloorsAdapter = new ArrayAdapter<>(requireNonNull(getContext()),
                R.layout.spinner_item, availableFloors);

        floorSpinner.setAdapter(availableFloorsAdapter);

        AdapterView.OnItemSelectedListener floorSpinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedFloor = Integer.parseInt(floorSpinner.getSelectedItem().toString());
                availableTablesAdapter = new ArrayAdapter<>(requireNonNull(getContext()),
                        R.layout.spinner_item, requireNonNull(availableTablesAtFloors.get(selectedFloor)));

                tableNumberSpinner.setAdapter(availableTablesAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        floorSpinner.setOnItemSelectedListener(floorSpinnerListener);

        takeSeatButton = requireNonNull(getView()).findViewById(R.id.takeSeatButton);

        takeSeatButton.setOnClickListener(v -> {
            int floor = Integer.parseInt(floorSpinner.getSelectedItem().toString());
            int table = Integer.parseInt(tableNumberSpinner.getSelectedItem().toString());

            TakeSeatTask takeSeatTask = new TakeSeatTask(floor, table);
            takeSeatTask.execute();
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new TakeSeatFragment();
        FragmentTransaction ft =
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations
                                (android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @SuppressLint("StaticFieldLeak")
    private class TakeSeatTask extends AsyncTask<Void, Void, Integer> {
        int floor;
        int table;
        int took = 0;

        TakeSeatTask(int floor, int table) {
            this.floor = floor;
            this.table = table;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            JSONObject obj;
            String updateURL = Constants.webserviceURL + "take_seat.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password, "UTF-8")
                        + "&" + URLEncoder.encode("floor", "UTF-8")
                        + "=" + URLEncoder.encode(String.valueOf(floor), "UTF-8")
                        + "&" + URLEncoder.encode("table", "UTF-8")
                        + "=" + URLEncoder.encode(String.valueOf(table), "UTF-8");
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
                    took = Integer.parseInt(obj.get("took").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return took;
        }

        @Override
        protected void onPostExecute(Integer took) {
            checkTakeSuccess(took, floor, table);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkTakeSuccess(int took, int floor, int table) {
        if (took == 1) {
            seatTaken = true;
            takenSeatId = table;
            takenSeatFloor = floor;

            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Pomyślnie zajęto miejsce", Toast.LENGTH_LONG).show();

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
        } else if (took == -1) {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Miejsce już zajęte", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Spróbuj ponownie", Toast.LENGTH_LONG).show();
        }
    }

}