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

import static com.example.buwing.RegisterActivity.notAllowedCharacterPattern;
import static com.example.buwing.RegisterActivity.notAllowedCharacterMessage;

public class InvitationListFragment extends BaseFragment {

    EditText loginEditText;
    TextView invitationTextView;
    Button searchButton;

    static ArrayList<String> invitations = new ArrayList<>();
    ListView listView;
    InvitationAdapter adapter;

    String friendLogin, status;
  
    static String invitation = "Brak zaproszeń";
    final String emptyFieldMessage = "Uzupełnij pole login";

    @SuppressLint("StaticFieldLeak")
    private class GetInvitation extends AsyncTask<Void, Void, Void> {

        GetInvitation() {}

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject obj;
            JSONArray array;
            int res;
            StringBuilder response = new StringBuilder();
            String invitationURL = Constants.webserviceURL + "invitation_info.php";

            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("login", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.password, "UTF-8");
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
                    res = Integer.parseInt(obj.get("result").toString());
                    if (res == 1) {
                        array = obj.getJSONArray("users");
                        for (int i = 0; i < array.length(); i++) {
                            String friend = array.get(i).toString();
                            if (!invitations.contains(friend))
                                invitations.add(friend);
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
            if (invitations.size() == 0)
                invitation = "Brak zaproszeń";
            else
                invitation = "Zaproszenia";
            invitationTextView.setText(invitation);
            adapter = new InvitationAdapter(getActivity(), R.layout.invitation_row, invitations);
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
            String confirmURL = Constants.webserviceURL + "confirm_invitation.php";
            URLConnection conn;
            inviterLogin = strings[0];

            try {
                String POSTdata = URLEncoder.encode("myLogin", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.password, "UTF-8")
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
        }
        MainScreenFragment.GetPendingInvitationsCountTask getPendingInvitationsCountTask = new MainScreenFragment.GetPendingInvitationsCountTask();
        getPendingInvitationsCountTask.execute();
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
            String confirmURL = Constants.webserviceURL + "delete_invitation.php";
            URLConnection conn;
            inviterLogin = strings[0];

            try {
                String POSTdata = URLEncoder.encode("myLogin", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.password, "UTF-8")
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
            Toast.makeText(getContext(), "Odrzucono zaproszenie", Toast.LENGTH_LONG).show();
        }
        MainScreenFragment.GetPendingInvitationsCountTask getPendingInvitationsCountTask = new MainScreenFragment.GetPendingInvitationsCountTask();
        getPendingInvitationsCountTask.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class AddFriendTask extends AsyncTask<Void, Void, Boolean> {

        private boolean sent;

        AddFriendTask(){}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject obj;
            String addURL = Constants.webserviceURL + "add_friend.php";
            StringBuilder response = new StringBuilder();
            URLConnection conn;

            try {
                String POSTdata = URLEncoder.encode("myLogin", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.login, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(MainActivity.password, "UTF-8")
                        + "&" + URLEncoder.encode("friendLogin", "UTF-8")
                        + "=" + URLEncoder.encode(friendLogin, "UTF-8");
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
                        "Zaproszenie wysłano wcześniej", Toast.LENGTH_LONG).show();
            }
            else if (status.equals("pending_invitation")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Masz oczekujące zaproszenie od użytkownika", Toast.LENGTH_LONG).show();
            }
            else if (status.equals("already_friends")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Już jesteście znajomymi", Toast.LENGTH_LONG).show();
            }
            else if (status.equals("myself")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Nie możesz zaprosić samego siebie", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    "Pomyślnie wysłano zaproszenie", Toast.LENGTH_LONG).show();
        }
    }

    private class InvitationAdapter extends ArrayAdapter<String> {

        private int layoutResourceId;
        private Context context;

        private class ViewHolder {
            TextView loginTextView;
            Button confirmButton, deleteButton;
        }

        private InvitationAdapter(Context context, int layoutResourceId, ArrayList<String> list) {
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
                viewHolder.confirmButton = row.findViewById(R.id.confirmButton);
                viewHolder.deleteButton = row.findViewById(R.id.deleteButton);
                viewHolder.confirmButton.setTag(position);
                viewHolder.deleteButton.setTag(position);
                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.loginTextView.setText(login);
            viewHolder.confirmButton.setOnClickListener(v -> {
                ConfirmInvitationTask confirmInvitationTask = new ConfirmInvitationTask();
                confirmInvitationTask.execute(getItem(position));
                invitations.remove(getItem(position));
                if (invitations.size() == 0)
                    invitation = "Brak zaproszeń";
                adapter.notifyDataSetChanged();
            });
            viewHolder.deleteButton.setOnClickListener(v -> {
                DeleteInvitationTask deleteInvitationTask = new DeleteInvitationTask();
                deleteInvitationTask.execute(getItem(position));
                invitations.remove(adapter.getItem(position));
                if (invitations.size() == 0)
                    invitation = "Brak zaproszeń";
                adapter.notifyDataSetChanged();
            });

            return row;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_invitation_list;
        title = "zaproszenia";
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
        invitationTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.invitationTextView);
        invitationTextView.setText(invitation);

        searchButton.setOnClickListener(v -> {
            friendLogin = loginEditText.getText().toString();

            Matcher loginMatcher = notAllowedCharacterPattern.matcher(friendLogin);

            if (friendLogin.isEmpty()) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        emptyFieldMessage, Toast.LENGTH_LONG).show();
            } else if (loginMatcher.find()) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        notAllowedCharacterMessage + "login", Toast.LENGTH_LONG).show();
            } else {
                AddFriendTask addFriendTask = new AddFriendTask();
                addFriendTask.execute();
            }

        });
    }
}
