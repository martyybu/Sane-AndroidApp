package com.example.tp.sane.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Models.DefaultResponse;
import com.example.tp.sane.R;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivityUser extends AppCompatActivity implements OnClickListener {

    private EditText EditTextUsername, EditTextPassword, EditTextEmailAddress;
    private ProgressDialog ProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        EditTextUsername = findViewById(R.id.EditTextUsername);
        EditTextPassword = findViewById(R.id.EditTextPassword);
        EditTextEmailAddress = findViewById(R.id.EditTextEmailAddress);

        ProgressDialog = new ProgressDialog(this);

        findViewById(R.id.ButtonSignUp).setOnClickListener(this);
        findViewById(R.id.TextViewBackToLogin).setOnClickListener(this);
    }

    //Registering the user
    private void userSignUp() {
        //Taking the values from the UI
        final String email = EditTextEmailAddress.getText().toString().trim();
        final String username = EditTextUsername.getText().toString().trim();
        String password = EditTextPassword.getText().toString().trim();

        //Validating the form
        if(username.length() >= 20) {
            EditTextUsername.setError("Username can only be 20 characters long.");
            EditTextUsername.requestFocus();
            return;
        }
        if(username.isEmpty()) {
            EditTextUsername.setError("Username is required");
            EditTextUsername.requestFocus();
            return;
        }
        if(username.length() < 5) {
            EditTextUsername.setError("Username must be at least 5 characters long.");
            EditTextUsername.requestFocus();
            return;
        }
        if(email.isEmpty()) {
            EditTextEmailAddress.setError("Email is required");
            EditTextEmailAddress.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EditTextEmailAddress.setError("Enter a valid email");
            EditTextEmailAddress.requestFocus();
            return;
        }
        if(email.length() > 60) {
            EditTextEmailAddress.setError("Email cannot be longer than 60 characters.");
            EditTextEmailAddress.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            EditTextPassword.setError("Password required");
            EditTextPassword.requestFocus();
            return;
        }
        if(password.length() < 7) {
            EditTextPassword.setError("Password should be at least 7 characters long.");
            EditTextPassword.requestFocus();
            return;
        }

        //Setting a progress dialog
        ProgressDialog.setMessage("Registering User...");
        ProgressDialog.show();

        //Calling the API
        Call<DefaultResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .createUser(username, password, email);

        //Waiting for a response
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                //Dismissing the progress dialog
                ProgressDialog.dismiss();

                if(response.code() == 201) {
                    DefaultResponse dr = response.body(); //Storing the response
                    Toast.makeText(RegisterActivityUser.this, dr.getMsg(), Toast.LENGTH_LONG).show();
                    //Wait 1 second before redirecting to login activity
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        startActivity(new Intent(RegisterActivityUser.this, LoginActivity.class));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 422) {
                    Toast.makeText(RegisterActivityUser.this, "User with this username or email already exist.", Toast.LENGTH_LONG).show();
                } else if (response.code() == 423) {
                    Toast.makeText(RegisterActivityUser.this, "Error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                ProgressDialog.dismiss();
            }
        });
    }

    //Click listeners for sign up button and back to login TextView
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ButtonSignUp:
                userSignUp();
                break;
            case R.id.TextViewBackToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
