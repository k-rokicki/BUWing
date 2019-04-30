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
import android.view.textservice.TextInfo;
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
import static java.util.Objects.requireNonNull;

public class TakeSeatScanBarcodeFragment extends BaseFragment {

    static String barcodeString = null;

    TextView floorTextView;
    TextView tableNumberTextView;
    Button takeSeatButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        MainScreenFragment.CheckSeatTakenTask checkSeatTakenTask = new MainScreenFragment.CheckSeatTakenTask();
        checkSeatTakenTask.execute();
        _layout = R.layout.fragment_take_seat_scan_barcode;
        title = "zajmij miejsce";
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_take_seat_scan_barcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireNonNull(getActivity()).setTitle("zajmij miejsce");

        floorTextView = requireNonNull(getView()).findViewById(R.id.floorTextView);
        tableNumberTextView = requireNonNull(getView()).findViewById(R.id.tableNumberTextView);
        takeSeatButton = requireNonNull(getView()).findViewById(R.id.takeSeatButton);

        floorTextView.setText(barcodeString);
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

}