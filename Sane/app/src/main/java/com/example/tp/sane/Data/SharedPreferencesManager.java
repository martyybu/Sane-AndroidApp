package com.example.tp.sane.Data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tp.sane.Models.User;

public class SharedPreferencesManager {

    private static final String SHARED_PREF_NAME = "my_shared_preff";
    private static SharedPreferencesManager mInstance;
    private Context context;

    private SharedPreferencesManager(Context context) {
        this.context = context;
    }


    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new SharedPreferencesManager(context);
        }
        return mInstance;
    }

    //Saves the user credentials
    public void saveUser(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("ID", user.getID());
        editor.putString("username", user.getUsername());
        editor.putString("email", user.getEmail());

        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("ID", -1) != -1;
    }

    //Gets user credentials
    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new User(
                sharedPreferences.getInt("ID", -1),
                sharedPreferences.getString("username", null),
                sharedPreferences.getString("email", null)
        );
    }

    //Clears user credentials(used for logout)
    public void clear(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
