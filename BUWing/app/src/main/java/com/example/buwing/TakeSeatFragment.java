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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import static com.example.buwing.MainScreenFragment.isLibraryOpen;
import static com.example.buwing.MainScreenFragment.takeSeatFreeMenuItemString;
import static com.example.buwing.MainScreenFragment.takeSeatTakenMenuItemString;
import static java.util.Objects.requireNonNull;

public class TakeSeatFragment extends BaseFragment {

    @SuppressLint("StaticFieldLeak")
    static TextView floorTextView;
    @SuppressLint("StaticFieldLeak")
    static TextView tableNumberTextView;

    Button releaseSeatButton;

    static final String defaultTextViewString = "-";
    static String floorTextViewString = defaultTextViewString;
    static String tableNumberTextViewString = defaultTextViewString;

    Button scanBarcodeButton;
    Button chooseFromMapButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isLibraryOpen) {
            _layout = R.layout.fragment_library_closed;
        } else {
            if (!seatTaken) {
                _layout = R.layout.fragment_take_seat;
                title = takeSeatFreeMenuItemString;
            } else {
                _layout = R.layout.fragment_seat_taken;
                title = takeSeatTakenMenuItemString;
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!isLibraryOpen)
            return inflater.inflate(R.layout.fragment_library_closed, container, false);
        else
            if (!seatTaken)
                return inflater.inflate(R.layout.fragment_take_seat, container, false);
            else
                return inflater.inflate(R.layout.fragment_seat_taken, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireNonNull(getActivity()).setTitle("zajmij miejsce");

        if (isLibraryOpen) {
            if (!seatTaken) {
                scanBarcodeButton = requireNonNull(getView()).findViewById(R.id.scanBarcodeButton);
                chooseFromMapButton = requireNonNull(getView()).findViewById(R.id.chooseFromMapButton);

                scanBarcodeButton.setOnClickListener(v -> {
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(TakeSeatFragment.this);
                    integrator.setPrompt("Skanuj kod ze stolika");
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();
                });

                chooseFromMapButton.setOnClickListener(v -> {
                    //TODO połączyć z mapą
                    Fragment fragment = new MapFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                });

                new MainScreenFragment.CheckSeatTakenTask().execute();

            } else {
                floorTextView = requireNonNull(getView()).findViewById(R.id.floorTextView);
                tableNumberTextView = requireNonNull(getView()).findViewById(R.id.tableNumberTextView);
                releaseSeatButton = requireNonNull(getView()).findViewById(R.id.releaseSeatButton);

                floorTextViewString = String.valueOf(takenSeatFloor);
                tableNumberTextViewString = String.valueOf(takenSeatId);

                floorTextView.setText(floorTextViewString);
                tableNumberTextView.setText(tableNumberTextViewString);

                releaseSeatButton.setOnClickListener(v -> {
                    ReleaseSeatTask releaseSeatTask = new ReleaseSeatTask();
                    releaseSeatTask.execute();
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            TakeSeatScanBarcodeFragment.barcodeString = result.getContents();
            Fragment fragment = new TakeSeatScanBarcodeFragment();
            FragmentTransaction ft =
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                            beginTransaction().
                            setCustomAnimations
                                    (R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ReleaseSeatTask extends AsyncTask<Void, Void, Boolean> {
        private boolean released = false;

        ReleaseSeatTask() {}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            String updateURL = Constants.webserviceURL + "release_seat.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(login, "UTF-8")
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