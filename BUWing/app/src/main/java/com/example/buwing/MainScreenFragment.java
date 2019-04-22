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

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.password;
import static com.example.buwing.MainActivity.seatTaken;
import static com.example.buwing.MainActivity.takenSeatFloor;
import static com.example.buwing.MainActivity.takenSeatId;
import static java.lang.Integer.*;
import static java.util.Objects.requireNonNull;

public class MainScreenFragment extends BaseFragment {

    @SuppressLint("StaticFieldLeak")
    static TextView fullnessInfoTextView;
    TextView openingHoursMsgTextView;
    @SuppressLint("StaticFieldLeak")
    static TextView openingHoursTextView;
    @SuppressLint("StaticFieldLeak")
    static TextView friendsInsideMsgTextView;
    @SuppressLint("StaticFieldLeak")
    static TextView friendsInsideTextView;

    static final String defaultFullnessInfoString = "- / -";
    static final String defaultOpeningHoursString = "brak danych";
    static final String defaultFriendsInsideString = "-";
    static final String defaultFriendsInsideMsgString_more = "znajomych w BUW";
    static final String defaultFriendsInsideMsgString_one = "znajomy w BUW";
    static final String defaultInvitationsMenuItemString = "zaproszenia";
    static int inactiveColor = Color.parseColor("#727272");
    static int activeColor = Color.parseColor("#10674F");

    static String fullnessInfoString = defaultFullnessInfoString;
    static int fullnessInfoColor = inactiveColor;
    static String openingHoursString = defaultOpeningHoursString;
    static int openingHoursColor = inactiveColor;
    static String friendsInsideString = defaultFriendsInsideString;
    static int friendsInsideColor = inactiveColor;
    static String friendsInsideMsgString = defaultFriendsInsideMsgString_more;

    static String invitationsMenuItemString = defaultInvitationsMenuItemString;

    static int opensHour;
    static int opensMinutes;
    static int closesHour;
    static int closesMinutes;
    static boolean closesNextDay;
    static boolean isLibraryOpen;
    static int freeSeatsCount;
    static int allSeatsCount;
    static int friendsInsideCount;
    static int pendingInvitationsCount = 0;

    static ArrayList<Integer> availableFloors = new ArrayList<>();
    @SuppressLint("UseSparseArrays")
    static HashMap<Integer, ArrayList<Integer>> availableTablesAtFloors = new HashMap<>();

    @SuppressLint("StaticFieldLeak")
    protected static class GetOpeningHoursTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject obj;
        private String response;

        GetOpeningHoursTask() {}

        @Override
        protected Boolean doInBackground(Void... voids) {
            String checkURL = Constants.webserviceURL + "opening_hours.php";

            HttpURLConnection conn = null;

            try {
                URL url = new URL(checkURL);
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
                    opensHour = parseInt(obj.getString("opensHour"));
                    opensMinutes = parseInt(obj.getString("opensMinutes"));
                    closesHour = parseInt(obj.getString("closesHour"));
                    closesMinutes = parseInt(obj.getString("closesMinutes"));
                    closesNextDay = Boolean.parseBoolean(obj.getString("closesNextDay"));
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
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
                    GetFriendsInsideCountTask getFriendsInsideCountTask = new GetFriendsInsideCountTask();
                    getFriendsInsideCountTask.execute();
                    GetAvailableTablesTask getAvailableTablesTask = new GetAvailableTablesTask();
                    getAvailableTablesTask.execute();
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
            GetPendingInvitationsCountTask getPendingInvitationsCountTask = new GetPendingInvitationsCountTask();
            getPendingInvitationsCountTask.execute();
            CheckSeatTakenTask checkSeatTakenTask = new CheckSeatTakenTask();
            checkSeatTakenTask.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class GetFullnessInfoTask extends AsyncTask<Void, Void, Boolean> {

        GetFullnessInfoTask() {}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            String response = null;
            String checkURL = Constants.webserviceURL + "fullness_info.php";

            HttpURLConnection conn = null;

            try {
                URL url = new URL(checkURL);
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
                    freeSeatsCount = parseInt(obj.get("freeSeatsCount").toString());
                    allSeatsCount = parseInt(obj.get("allSeatsCount").toString());
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
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
    protected static class CheckSeatTakenTask extends AsyncTask<Void, Void, Boolean> {

        CheckSeatTakenTask() {}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            StringBuilder response = new StringBuilder();
            String checkURL = Constants.webserviceURL + "seat_taken.php";
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
                    takenSeatId = parseInt(obj.get("seatId").toString());
                    takenSeatFloor = parseInt(obj.get("seatFloor").toString());
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class GetFriendsInsideCountTask extends AsyncTask<Void, Void, Boolean> {
        GetFriendsInsideCountTask() {}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            StringBuilder response = new StringBuilder();
            String checkURL = Constants.webserviceURL + "friends_inside_count.php";
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
                    friendsInsideCount = parseInt(obj.get("friendsCount").toString());
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                friendsInsideString = defaultFriendsInsideString;
                friendsInsideColor = inactiveColor;
                friendsInsideMsgString = defaultFriendsInsideMsgString_more;
            } else {
                friendsInsideString = String.format("%d", friendsInsideCount);
                friendsInsideColor = activeColor;
                if (friendsInsideCount != 1) {
                    friendsInsideMsgString = defaultFriendsInsideMsgString_more;
                } else {
                    friendsInsideMsgString = defaultFriendsInsideMsgString_one;
                }
            }
            friendsInsideTextView.setText(friendsInsideString);
            friendsInsideTextView.setTextColor(friendsInsideColor);
            friendsInsideMsgTextView.setText(friendsInsideMsgString);
        }
    }

    @SuppressLint("StaticFieldLeak")
    protected static class GetPendingInvitationsCountTask extends AsyncTask<Void, Void, Boolean> {

        GetPendingInvitationsCountTask() {}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            StringBuilder response = new StringBuilder();
            String checkURL = Constants.webserviceURL + "pending_invitations_count.php";
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
                    pendingInvitationsCount = parseInt(obj.get("invitationsCount").toString());
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Boolean result) {
            if (result && pendingInvitationsCount != 0) {
                invitationsMenuItemString = String.format("%s (%d)", defaultInvitationsMenuItemString, pendingInvitationsCount);
            } else {
                invitationsMenuItemString = defaultInvitationsMenuItemString;
            }
            LoggedInActivity.updateInvitationMenuItem();
        }
    }

    @SuppressLint("StaticFieldLeak")
    static class GetAvailableTablesTask extends AsyncTask<Void, Void, Void> {

        GetAvailableTablesTask() {}

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject obj;
            StringBuilder response = new StringBuilder();
            String checkURL = Constants.webserviceURL + "available_tables.php";
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
                availableFloors.clear();
                availableTablesAtFloors.clear();
                try {
                    obj = new JSONObject(result);
                    JSONArray availableFloorsArray = obj.getJSONArray("availableFloors");
                    if (availableFloorsArray != null) {
                        for (int i = 0; i < availableFloorsArray.length(); ++i) {
                            availableFloors.add(valueOf(availableFloorsArray.getString(i)));
                        }
                    }
                    JSONObject availableTablesAtFloorsObject = obj.getJSONObject("availableTables");
                    for (int i = 0; i < availableFloors.size(); ++i) {
                        int floor = availableFloors.get(i);
                        ArrayList<Integer> tables = new ArrayList<>();
                        JSONArray tablesArray = availableTablesAtFloorsObject.getJSONArray(String.valueOf(floor));
                        for (int j = 0; j < tablesArray.length(); ++j) {
                            tables.add(tablesArray.getInt(j));
                        }
                        availableTablesAtFloors.put(floor, tables);
                    }
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
        friendsInsideMsgTextView = requireNonNull(getView().findViewById(R.id.friendsInsideMsgTextView));
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
        friendsInsideMsgTextView.setText(friendsInsideMsgString);

        GetOpeningHoursTask getOpeningHoursTask = new GetOpeningHoursTask();
        getOpeningHoursTask.execute();
    }

    @Override
    public void onBackPressed() {
        Intent closeApp = new Intent(Intent.ACTION_MAIN);
        closeApp.addCategory(Intent.CATEGORY_HOME);
        closeApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(closeApp);
    }
}