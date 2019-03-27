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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FriendsListFragment extends BaseFragment {

    EditText loginEditText;
    Button searchButton;

    ArrayList<String> arr = new ArrayList<>();
    ListView listView;
    InvitationAdapter adapter;

    String friend_login, status;

    String notAllowedCharacterPatternString = "^.*['\"();].*$";
    Pattern notAllowedCharacterPattern = Pattern.compile(notAllowedCharacterPatternString);
    String notAllowedCharacterMessage = "Niedozwolony znak w polu login";

    @SuppressLint("StaticFieldLeak")
    private class GetInvitation extends AsyncTask<Void, Void, Void> {

        GetInvitation() {}

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject obj;
            JSONArray array;
            StringBuilder response = new StringBuilder();
            String invitationURL = "https://students.mimuw.edu.pl/~mk394389/buwing/invitation_info.php";

            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8");
                URL url = new URL(invitationURL);
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
                    array = obj.getJSONArray("users");
                    for (int i = 0; i < array.length(); i++) {
                        arr.add(array.getString(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new InvitationAdapter(getActivity(), R.layout.invitation_row, arr);
            listView.setAdapter(adapter);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ConfirmInvitationTask extends AsyncTask<String, Void, Boolean> {

        private String inviterLogin;
        private boolean success;

        ConfirmInvitationTask() {}

        @Override
        protected Boolean doInBackground(String... strings) {
            JSONObject obj;
            String response = null;
            String confirmURL = "http://students.mimuw.edu.pl/~mk394389/buwing/confirm_invitation.php";
            URLConnection conn;
            inviterLogin = strings[0];

            try {
                String POSTdata = URLEncoder.encode("myLogin", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("inviterLogin", "UTF-8")
                        + "=" + URLEncoder.encode(inviterLogin, "UTF-8");
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
            checkConfirmSuccess(success);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkConfirmSuccess(Boolean success) {
        if (!success) {
            Toast.makeText(getContext(), "Nie powiodło się potwierdzenie", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Jesteście znajomymi", Toast.LENGTH_LONG).show();
            GetInvitation getInvitation = new GetInvitation();
            getInvitation.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteInvitationTask extends AsyncTask<String, Void, Boolean> {

        private String inviterLogin;
        private boolean success;

        DeleteInvitationTask() {}

        @Override
        protected Boolean doInBackground(String... strings) {
            JSONObject obj;
            String response = null;
            String confirmURL = "http://students.mimuw.edu.pl/~mk394389/buwing/delete_invitation.php";
            URLConnection conn;
            inviterLogin = strings[0];

            try {
                String POSTdata = URLEncoder.encode("myLogin", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("inviterLogin", "UTF-8")
                        + "=" + URLEncoder.encode(inviterLogin, "UTF-8");
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkDeleteSuccess(Boolean success) {
        if (!success) {
            Toast.makeText(getContext(), "Nie powiodło się usunięcie", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Odrzuciłeś zaproszenie", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AddFriendTask extends AsyncTask<Void, Void, Boolean> {

        private boolean sent;

        AddFriendTask(){}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            String addURL = "http://students.mimuw.edu.pl/~mk394389/buwing/add_friend.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("my_login", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("my_name", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.name, "UTF-8")
                        + "&" + URLEncoder.encode("my_surname", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.surname, "UTf-8")
                        + "&" + URLEncoder.encode("friend_login", "UTF-8")
                        + "=" + URLEncoder.encode(friend_login, "UTF-8");
                URL url = new URL(addURL);

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
                    sent = Boolean.parseBoolean(obj.get("sent").toString());
                    status = obj.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return sent;
        }

        @Override
        protected void onPostExecute(Boolean sent) {
            checkInvitationSuccess(sent);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkInvitationSuccess(Boolean sent) {
        if (!sent) {
            if (status.equals("no_user")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Nie ma takiego użytkownika", Toast.LENGTH_LONG).show();
            }
            else if (status.equals("already_sent")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Już wysłałeś zaproszenie", Toast.LENGTH_LONG).show();
            }
            else if (status.equals("pending_invitation")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Masz oczekujące zaproszenie od użytkownika", Toast.LENGTH_LONG).show();
            }
            else if (status.equals("already_friends")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Już jesteście znajomymi", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Pomyślnie wysłano zaproszenie", Toast.LENGTH_LONG).show();
        }
    }

    private class InvitationAdapter extends ArrayAdapter<String> {

        private ArrayList<String> list;
        private int layoutResourceId;
        private Context context;

        private class ViewHolder {
            TextView loginTextView;
            Button confirmButton, deleteButton;
        }

        private InvitationAdapter(Context context, int layoutResourceId, ArrayList<String> list) {
            super(context, layoutResourceId, list);
            this.list = list;
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
                viewHolder.confirmButton = row.findViewById(R.id.confirmButton);
                viewHolder.deleteButton = row.findViewById(R.id.deleteButton);
                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.loginTextView.setText(login);
            viewHolder.confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmInvitationTask confirmInvitationTask = new ConfirmInvitationTask();
                    confirmInvitationTask.execute(getItem(position));
                    arr.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteInvitationTask deleteInvitationTask = new DeleteInvitationTask();
                    deleteInvitationTask.execute(getItem(position));
                    arr.remove(position);
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

        GetInvitation getInvitation = new GetInvitation();
        getInvitation.execute();

        listView = Objects.requireNonNull(getActivity()).findViewById(R.id.invitationListView);

        loginEditText = Objects.requireNonNull(getActivity()).findViewById(R.id.loginEditText);
        searchButton = Objects.requireNonNull(getActivity()).findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_login = loginEditText.getText().toString();

                Matcher loginMatcher = notAllowedCharacterPattern.matcher(friend_login);

                if (friend_login.isEmpty()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            "Uzupełnij pole login", Toast.LENGTH_LONG).show();
                } else if (loginMatcher.find()) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            notAllowedCharacterMessage, Toast.LENGTH_LONG).show();
                } else {
                    AddFriendTask addFriendTask = new AddFriendTask();
                    addFriendTask.execute();
                }

            }
        });
    }
}