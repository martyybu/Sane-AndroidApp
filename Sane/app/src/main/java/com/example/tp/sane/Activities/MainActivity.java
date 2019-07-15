package com.example.tp.sane.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Fragments.ChatFragment;
import com.example.tp.sane.Fragments.DiaryFragment;
import com.example.tp.sane.Fragments.HomeFragment;
import com.example.tp.sane.Fragments.ProfileFragment;
import com.example.tp.sane.Fragments.SearchFragment;
import com.example.tp.sane.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appview);
        //Sets up bottom navigation menu
        BottomNavigationView navigationView = findViewById(R.id.Bottom_Navigation_Menu);
        navigationView.setOnNavigationItemSelectedListener(this);
        displayFragment(new HomeFragment());
    }

    //Displaying the fragments
    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RL_Container, fragment)
                .commit();
    }

    //If not logged in, redirects to login activity
    protected void onStart() {
        super.onStart();

        if(!SharedPreferencesManager.getInstance(this).isLoggedIn()) {
            Intent LoginActivity = new Intent(this, LoginActivity.class);
            LoginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(LoginActivity);
        }
    }

    //Click listener for the navigation menu at the bottom
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.menu_search:
                fragment = new SearchFragment();
                break;
            case R.id.menu_chat:
                fragment = new ChatFragment();
                break;
            case R.id.menu_home:
                fragment = new HomeFragment();
                break;
            case R.id.menu_diary:
                fragment = new DiaryFragment();
                break;
            case R.id.menu_profile:
                fragment = new ProfileFragment();
                break;
        }

        if (fragment != null) {
            displayFragment(fragment);
        }

        return false;
    }
}
