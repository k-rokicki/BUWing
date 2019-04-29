package com.example.buwing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class FriendsListFragment extends BaseFragment {

    static ArrayList<String> friends = new ArrayList<>();
    ListView listView;
    FriendsAdapter adapter;

    @SuppressLint("StaticFieldLeak")
    private class GetFriends extends AsyncTask<Void, Void, Void> {

        GetFriends() {}

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject obj;
            JSONArray array;
            int success;
            StringBuilder response = new StringBuilder();
            String friendsURL = Constants.webserviceURL + "friends_info.php";

            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UtF-8")
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
                        array = obj.getJSONArray("friends");
                        for (int i = 0; i < array.length(); i++) {
                            String friend = array.get(i).toString();
                            if (!friends.contains(friend))
                                friends.add(array.getString(i));
                        }
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

    @SuppressWarnings("StaticFieldLeak")
    private class DeleteFriendTask extends AsyncTask<String, Void, Boolean> {

        private String friendLogin;
        private boolean success;

        DeleteFriendTask() {}

        @Override
        protected Boolean doInBackground(String... strings) {
            JSONObject obj;
            String response = null;
            String confirmURL = Constants.webserviceURL + "delete_friend.php";
            URLConnection conn;
            friendLogin = strings[0];

            try {
                String POSTdata = URLEncoder.encode("myLogin", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.password, "UTF-8")
                        + "&" + URLEncoder.encode("friendLogin", "UTF-8")
                        + "=" + URLEncoder.encode(friendLogin, "UTF-8");
                URL url = new URL(confirmURL);
                conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(POSTdata);
                wr.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = in.readLine();
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    obj = new JSONObject(response);
                    success = Boolean.parseBoolean(obj.get("success").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            checkDeleteSuccess(success);
        }
    }

    private void checkDeleteSuccess(Boolean success) {
        if (!success) {
            Toast.makeText(getContext(), "Nie powiodło się usunięcie", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Usunięto znajomego", Toast.LENGTH_LONG).show();
        }
    }

    private class FriendsAdapter extends ArrayAdapter<String> {

        private int layoutResourceId;
        private Context context;

        private class ViewHolder {
            TextView loginTextView;
            Button deleteButton;
        }

        private FriendsAdapter(Context context, int layoutResourceId, ArrayList<String> list) {
            super(context, layoutResourceId, list);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            final ViewHolder viewHolder;
            String login = getItem(position);
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.loginTextView = row.findViewById(R.id.loginTextView);
                viewHolder.deleteButton = row.findViewById(R.id.deleteButton);
                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.loginTextView.setText(login);
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteFriendTask deleteFriendTask = new DeleteFriendTask();
                    deleteFriendTask.execute(getItem(position));
                    friends.remove(getItem(position));
                    adapter.notifyDataSetChanged();
                }
            });
            return row;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_friends_list;
        title = "lista znajomych";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetFriends getFriends = new GetFriends();
        getFriends.execute();

        listView = Objects.requireNonNull(getActivity()).findViewById(R.id.friendsListView);
        adapter = new FriendsAdapter(getActivity(), R.layout.friend_row, friends);
        listView.setAdapter(adapter);
    }
}
