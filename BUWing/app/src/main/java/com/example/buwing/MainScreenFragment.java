package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.password;
import static com.example.buwing.MainActivity.seatTaken;
import static com.example.buwing.MainActivity.takenSeatFloor;
import static com.example.buwing.MainActivity.takenSeatId;
import static java.util.Objects.requireNonNull;

public class MainScreenFragment extends BaseFragment {

    TextView fullnessInfoTextView;
    TextView openingHoursMsgTextView;
    TextView openingHoursTextView;
    TextView friendsInsideTextView;

    static final String defaultFullnessInfoString = "- / -";
    static final String defaultOpeningHoursString = "brak danych";
    static final String defaultFriendsInsideString = "-";
    static int inactiveColor = Color.parseColor("#727272");
    static int activeColor = Color.parseColor("#10674F");

    static String fullnessInfoString = defaultFullnessInfoString;
    static int fullnessInfoColor = inactiveColor;
    static String openingHoursString = defaultOpeningHoursString;
    static int openingHoursColor = inactiveColor;
    static String friendsInsideString = defaultFriendsInsideString;
    static int friendsInsideColor = inactiveColor;

    int opensHour, opensMinutes, closesHour, closesMinutes;
    boolean closesNextDay;
    static boolean isLibraryOpen;
    int freeSeatsCount;
    int allSeatsCount;
    int friendsInsideCount;

    @SuppressLint("StaticFieldLeak")
    private class GetOpeningHoursTask extends AsyncTask<Void, Void, Void> {
        private JSONObject obj;
        private String response;

        GetOpeningHoursTask() {
            response = null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String loginURL = Constants.webserviceURL + "opening_hours.php";

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
                    response = in.readLine();
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
                try {
                    obj = new JSONObject(response);
                    opensHour = Integer.parseInt(obj.getString("opensHour"));
                    opensMinutes = Integer.parseInt(obj.getString("opensMinutes"));
                    closesHour = Integer.parseInt(obj.getString("closesHour"));
                    closesMinutes = Integer.parseInt(obj.getString("closesMinutes"));
                    closesNextDay = Boolean.parseBoolean(obj.getString("closesNextDay"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Void result) {
            if (response == null) {
                openingHoursString = defaultOpeningHoursString;
                openingHoursColor = inactiveColor;
            } else {
                Calendar rightNow = Calendar.getInstance();
                int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                int currentMinutes = rightNow.get(Calendar.MINUTE);

                if (!closesNextDay) {
                    if (currentHour > opensHour || (currentHour == opensHour && currentMinutes >= opensMinutes)) {
                        if (currentHour < closesHour || (currentHour == closesHour && currentMinutes < closesMinutes)) {
                            isLibraryOpen = true;
                        } else {
                            isLibraryOpen = false;
                        }
                    } else {
                        isLibraryOpen = false;
                    }
                } else {
                    if (currentHour > opensHour || (currentHour == opensHour && currentMinutes >= opensMinutes)) {
                        isLibraryOpen = true;
                    } else if (currentHour < closesHour || (currentHour == closesHour && currentMinutes < closesMinutes)) {
                        isLibraryOpen = true;
                    } else {
                        isLibraryOpen = false;
                    }
                }

                if (isLibraryOpen) {
                    GetFullnessInfoTask getFullnessInfoTask = new GetFullnessInfoTask();
                    getFullnessInfoTask.execute();
                    //TODO GetFriendsInsideInfoTask
                } else {
                    fullnessInfoString = defaultFullnessInfoString;
                    friendsInsideString = defaultFriendsInsideString;
                    fullnessInfoColor = inactiveColor;
                    friendsInsideColor = inactiveColor;

                    fullnessInfoTextView.setText(fullnessInfoString);
                    friendsInsideTextView.setText(friendsInsideString);
                    fullnessInfoTextView.setTextColor(fullnessInfoColor);
                    friendsInsideTextView.setTextColor(friendsInsideColor);
                }

                openingHoursString = String.format("%d:%02d - %d:%02d",
                        opensHour, opensMinutes, closesHour, closesMinutes);
                openingHoursColor = isLibraryOpen ? activeColor : inactiveColor;
            }

            openingHoursTextView.setText(openingHoursString);
            openingHoursTextView.setTextColor(openingHoursColor);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetFullnessInfoTask extends AsyncTask<Void, Void, Void> {
        private JSONObject obj;
        private String response;

        GetFullnessInfoTask() {
            response = null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String loginURL = Constants.webserviceURL + "fullness_info.php";

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
                    response = in.readLine();
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
                try {
                    obj = new JSONObject(response);
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
            if (response == null) {
                fullnessInfoString = defaultFullnessInfoString;
                fullnessInfoColor = inactiveColor;
            } else {
                fullnessInfoString = String.format("%d / %d", freeSeatsCount, allSeatsCount);
                fullnessInfoColor = activeColor;
            }
            fullnessInfoTextView.setText(fullnessInfoString);
            fullnessInfoTextView.setTextColor(fullnessInfoColor);
        }
    }

    @SuppressLint("StaticFieldLeak")
    protected static class CheckSeatTakenTask extends AsyncTask<Void, Void, Void> {

        CheckSeatTakenTask() {}

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject obj;
            String checkURL = Constants.webserviceURL + "seat_taken.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password, "UTF-8");
                URL url = new URL(checkURL);

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
                    seatTaken = Boolean.parseBoolean(obj.get("taken").toString());
                    takenSeatId = Integer.parseInt(obj.get("seatId").toString());
                    takenSeatFloor = Integer.parseInt(obj.get("seatFloor").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile;
        title = "BUWing";
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireNonNull(getActivity()).setTitle("ekran główny");

        fullnessInfoTextView = requireNonNull(getView()).findViewById(R.id.fullnessInfoTextView);
        openingHoursMsgTextView = requireNonNull(getView()).findViewById(R.id.openingHoursMsgTextView);
        openingHoursTextView = requireNonNull(getView()).findViewById(R.id.openingHoursTextView);
        friendsInsideTextView = requireNonNull(getView()).findViewById(R.id.friendsInsideTextView);

        SpannableString openingHoursMsgString = new SpannableString("godziny otwarcia");
        openingHoursMsgString.setSpan(new UnderlineSpan(), 0, openingHoursMsgString.length(), 0);
        openingHoursMsgTextView.setText(openingHoursMsgString);

        fullnessInfoTextView.setTextColor(fullnessInfoColor);
        openingHoursTextView.setTextColor(openingHoursColor);
        friendsInsideTextView.setTextColor(friendsInsideColor);

        fullnessInfoTextView.setText(fullnessInfoString);
        openingHoursTextView.setText(openingHoursString);
        friendsInsideTextView.setText(friendsInsideString);

        GetOpeningHoursTask getOpeningHoursTask = new GetOpeningHoursTask();
        getOpeningHoursTask.execute();

        CheckSeatTakenTask checkSeatTakenTask = new CheckSeatTakenTask();
        checkSeatTakenTask.execute();
    }

    @Override
    public void onBackPressed() {
        Intent closeApp = new Intent(Intent.ACTION_MAIN);
        closeApp.addCategory(Intent.CATEGORY_HOME);
        closeApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(closeApp);
    }
}