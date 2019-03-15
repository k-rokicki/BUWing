package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class MyProfileFragment extends BaseFragment {

    TextView nameMsgTextView, surnameMsgTextView, loginMsgTextView;
    TextView nameTextView, surnameTextView, loginTextView;
    Button changeInfoButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile;
        title = "mój profil";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameMsgTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.nameMsgTextView);
        surnameMsgTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.surnameMsgTextView);
        loginMsgTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.loginMsgTextView);

        nameTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.nameTextView);
        surnameTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.surnameTextView);
        loginTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.loginTextView);

        changeInfoButton = Objects.requireNonNull(getActivity()).findViewById(R.id.changeInfoButton);

        nameTextView.setText(MainActivity.name);
        surnameTextView.setText(MainActivity.surname);
        loginTextView.setText(MainActivity.login);

        changeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyProfileChangeInfoFragment();
                FragmentTransaction ft =
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                                beginTransaction().
                                setCustomAnimations
                                        (android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });
    }

}