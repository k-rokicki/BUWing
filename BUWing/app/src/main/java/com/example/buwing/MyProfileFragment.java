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

    TextView nameTextView, surnameTextView, loginTextView, emailTextView;
    Button editAccountButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile;
        title = "mój profil";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.nameTextView);
        surnameTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.surnameTextView);
        loginTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.loginTextView);
        emailTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.emailTextView);

        editAccountButton = Objects.requireNonNull(getActivity()).findViewById(R.id.editAccountButton);

        nameTextView.setText(MainActivity.name);
        surnameTextView.setText(MainActivity.surname);
        loginTextView.setText(MainActivity.login);
        emailTextView.setText(MainActivity.email);

        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyProfileChangeFragment();
                FragmentTransaction ft =
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                                beginTransaction().
                                setCustomAnimations
                                        (R.anim.slide_in_right, R.anim.slide_out_left);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });
    }

}