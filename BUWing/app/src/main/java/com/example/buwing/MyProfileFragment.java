package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class MyProfileFragment extends BaseFragment {

    TextView nameMsgTextView, surnameMsgTextView, loginMsgTextView;
    TextView nameTextView, surnameTextView, loginTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile;
        title = "mój profil";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameMsgTextView = Objects.requireNonNull(getView()).findViewById(R.id.nameMsgTextView);
        surnameMsgTextView = Objects.requireNonNull(getView()).findViewById(R.id.surnameMsgTextView);
        loginMsgTextView = Objects.requireNonNull(getView()).findViewById(R.id.loginMsgTextView);

        nameTextView = Objects.requireNonNull(getView()).findViewById(R.id.nameTextView);
        surnameTextView = Objects.requireNonNull(getView()).findViewById(R.id.surnameTextView);
        loginTextView = Objects.requireNonNull(getView()).findViewById(R.id.loginTextView);

        nameTextView.setText(MainActivity.name);
        surnameTextView.setText(MainActivity.surname);
        loginTextView.setText(MainActivity.login);
    }

}