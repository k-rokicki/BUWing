package com.example.buwing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Objects;

import static com.example.buwing.MainScreenFragment.isLibraryOpen;

public class FriendsInsideFragment extends BaseFragment {

    static ArrayList<FriendRow> friends = new ArrayList<>();
    ListView listView;
    FriendsInsideAdapter adapter;

    private class FriendRow {

        private final String login;
        private final int floor;
        private final int seat;

        private FriendRow(String login, int floor, int seat) {
            this.login = login;
            this.floor = floor;
            this.seat = seat;
        }

        private String getLogin() {
            return login;
        }

        private int getFloor() {
            return floor;
        }

        private int getSeat() {
            return seat;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof FriendRow) {
                return (((FriendRow) obj).login.equals(this.login) && ((FriendRow) obj).floor == this.floor
                        && ((FriendRow) obj).seat == this.seat);
            }
            return false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetFriends extends AsyncTask<Void, Void, Void> {

        GetFriends() {}

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject obj;
            JSONArray array;
            int success;
            StringBuilder response = new StringBuilder();
            String friendsURL = Constants.webserviceURL + "friends_inside_info.php";

            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.password, "UTF-8");
                URL url = new URL(friendsURL);
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
                    success = Integer.parseInt(obj.get("result").toString());
                    if (success == 1) {
                        ArrayList<FriendRow> temp = new ArrayList<>();
                        array = obj.getJSONArray("friends");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            String login = jsonObject.get("login").toString();
                            int floor = Integer.parseInt(jsonObject.get("floor").toString());
                            int seat = Integer.parseInt(jsonObject.get("seat").toString());
                            FriendRow row = new FriendRow(login, floor, seat);
                            temp.add(row);
                        }
                        friends.clear();
                        friends.addAll(temp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }

    private class FriendsInsideAdapter extends ArrayAdapter<FriendRow> {

        private int layoutResourceId;
        private Context context;

        private class ViewHolder {
            TextView loginTextView, floorTextView, seatTextView;
        }

        private FriendsInsideAdapter(Context context, int layoutResourceId, ArrayList<FriendRow> list) {
            super(context, layoutResourceId, list);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable  View convertView, @NonNull ViewGroup parent) {
            final ViewHolder viewHolder;
            FriendRow friendRow = getItem(position);
            String login = friendRow.getLogin();
            int floor = friendRow.getFloor();
            int seat = friendRow.getSeat();

            if (convertView == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.loginTextView = convertView.findViewById(R.id.loginTextView);
                viewHolder.floorTextView = convertView.findViewById(R.id.floorTextView);
                viewHolder.seatTextView = convertView.findViewById(R.id.seatTextView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.loginTextView.setText(login);
            viewHolder.floorTextView.setText(String.valueOf(floor));
            viewHolder.seatTextView.setText(String.valueOf(seat));

            return convertView;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isLibraryOpen) {
            _layout = R.layout.fragment_library_closed;
        } else {
            _layout = R.layout.fragment_friends_inside;
        }
        title = "znajomi w BUW";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isLibraryOpen) {
            listView = Objects.requireNonNull(getActivity()).findViewById(R.id.friendsInsideListView);
            listView.addHeaderView(getLayoutInflater().inflate(R.layout.friends_inside_header, null));
            adapter = new FriendsInsideAdapter(getActivity(), R.layout.friend_inside_row, friends);
            listView.setAdapter(adapter);

            //noinspection StatementWithEmptyBody
            while (listView.getCount() - 1 != friends.size()) {
                // issue #30 fix (?)
            }

            new GetFriends().execute();
        }
    }
}