package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import static com.example.buwing.MainActivity.loginCredentials;
import static com.example.buwing.MainActivity.name;
import static com.example.buwing.MainActivity.surname;
import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainScreenFragment.invitationsMenuItemString;
import static com.example.buwing.MainScreenFragment.takeSeatMenuItemString;

public class LoggedInActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("StaticFieldLeak")
    static NavigationView navigationView;
    @SuppressLint("StaticFieldLeak")
    static TextView nameTextView;
    @SuppressLint("StaticFieldLeak")
    static TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("BUWing");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        View nameAndLoginView = navigationView.getHeaderView(0);
        nameTextView = nameAndLoginView.findViewById(R.id.nameTextView);
        loginTextView = nameAndLoginView.findViewById(R.id.loginTextView);

        nameTextView.setText(String.format("%s %s", name, surname));
        loginTextView.setText(login);

        displaySelectedScreen(R.id.nav_mainScreen);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            tellFragments();
        }
    }

    private void tellFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f instanceof BaseFragment)
                ((BaseFragment)f).onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        MainScreenFragment.GetPendingInvitationsCountTask getPendingInvitationsCountTask = new MainScreenFragment.GetPendingInvitationsCountTask();
        getPendingInvitationsCountTask.execute();
        MainScreenFragment.GetOpeningHoursTask getOpeningHoursTask = new MainScreenFragment.GetOpeningHoursTask();
        getOpeningHoursTask.execute();
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_mainScreen:
                fragment = new MainScreenFragment();
                break;
            case R.id.nav_takeSeat:
                fragment = new TakeSeatFragment();
                break;
            case R.id.nav_friendsInside:
                fragment = new FriendsInsideFragment();
                break;
            case R.id.nav_myProfile:
                fragment = new MyProfileFragment();
                break;
            case R.id.nav_invitationList:
                fragment = new InvitationListFragment();
                break;
            case R.id.nav_friendsList:
                fragment = new FriendsListFragment();
                break;
            case R.id.nav_logout:
                if (loginCredentials.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    loginCredentials.delete();
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public static void updateUserInfo() {
        nameTextView.setText(String.format("%s %s", name, surname));
        loginTextView.setText(login);
    }

    public static void updateInvitationMenuItem() {
        MenuItem invitationsMenuItem = navigationView.getMenu().findItem(R.id.nav_invitationList);
        invitationsMenuItem.setTitle(invitationsMenuItemString);
    }

    public static void updateTakeSeatMenuItem() {
        MenuItem takeSeatMenuItem = navigationView.getMenu().findItem(R.id.nav_takeSeat);
        takeSeatMenuItem.setTitle(takeSeatMenuItemString);

    }
}
