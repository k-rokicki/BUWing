package com.example.buwing;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;

public class InvitationAdapter extends ArrayAdapter<String> {

    private ArrayList<String> list;
    private int layoutResourceId;
    private Context context;

    static class ViewHolder {
        TextView loginTextView;
        Button confirmButton, deleteButton;
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
        ViewHolder viewHolder = null;
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
                Toast.makeText(context, "confirm " + position, Toast.LENGTH_LONG).show();
            }
        });
        viewHolder.deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete " + position, Toast.LENGTH_LONG).show();
            }
        });

        return row;
    }
}
