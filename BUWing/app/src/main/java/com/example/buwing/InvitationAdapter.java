package com.example.buwing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

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

public class InvitationAdapter extends ArrayAdapter<String> {

    private ArrayList<String> list;
    private int layoutResourceId;
    private Context context;

    static class ViewHolder {
        TextView loginTextView;
        Button confirmButton, deleteButton;
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

    public InvitationAdapter(Context context, int layoutResourceId, ArrayList<String> list) {
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
        viewHolder.confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmInvitationTask confirmInvitationTask = new ConfirmInvitationTask();
                confirmInvitationTask.execute(getItem(position));
            }
        });
        viewHolder.deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteInvitationTask deleteInvitationTask = new DeleteInvitationTask();
                deleteInvitationTask.execute(getItem(position));
            }
        });

        return row;
    }
}
