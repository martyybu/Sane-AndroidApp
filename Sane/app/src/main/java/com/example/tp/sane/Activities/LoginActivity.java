package com.example.tp.sane.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.LoginResponse;
import com.example.tp.sane.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText EditTextUsername, EditTextPassword;
    private ProgressDialog ProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditTextUsername = findViewById(R.id.EditTextUsername);
        EditTextPassword = findViewById(R.id.EditTextPassword);

        ProgressDialog = new ProgressDialog(this);

        findViewById(R.id.ButtonLogin).setOnClickListener(this);
        findViewById(R.id.TextViewForgotPassword).setOnClickListener(this);
        findViewById(R.id.ButtonSignUp).setOnClickListener(this);

    }

    //Starts a login session. The user will stay logged in until logged out
    protected void onStart() {
        super.onStart();

        if(SharedPreferencesManager.getInstance(this).isLoggedIn()) {
            Intent MainActivity = new Intent(LoginActivity.this, MainActivity.class);
            MainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(MainActivity);
        }
    }

    //Logging in the user
    private void userLogin() {

        String username = EditTextUsername.getText().toString().trim();
        String password = EditTextPassword.getText().toString().trim();

        //Validating the form (Most validation is done via PHP, so see the response messages there)
        if(username.isEmpty()) {
            EditTextUsername.setError("Username is required");
            EditTextUsername.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            EditTextPassword.setError("Password required");
            EditTextPassword.requestFocus();
            return;
        }

        ProgressDialog.setMessage("Just a minute...");
        ProgressDialog.show();

        //Calls the client
        Call<LoginResponse> call = RetrofitClient
                .getInstance().getApi().userLogin(username, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                ProgressDialog.dismiss();
                //Saves the response value
                LoginResponse loginResponse = response.body();

                //If error is not false, logs the user in.
                if(!loginResponse.isError()) {

                    //Saves the session
                    SharedPreferencesManager.getInstance(LoginActivity.this)
                            .saveUser(loginResponse.getUser());

                    //Opens the main activity
                    Intent MainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    MainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(MainActivity);

                    //Displays messages that come from PHP
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                ProgressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error occurred. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Click listeners for login and sign up.
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ButtonLogin:
                userLogin();
                break;
            case R.id.TextViewForgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.ButtonSignUp:
                startActivity(new Intent(this, RegisterSwitchActivity.class));
                break;
        }
    }
}
