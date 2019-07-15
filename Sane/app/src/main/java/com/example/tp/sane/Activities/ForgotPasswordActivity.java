package com.example.tp.sane.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.example.tp.sane.R;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    EditText EditTextEmailAddress;
    private ProgressDialog ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditTextEmailAddress = findViewById(R.id.EditTextEmailAddress);

        ProgressDialog = new ProgressDialog(this);

        //Setting listeners on buttons and textview
        findViewById(R.id.ButtonResetPassword).setOnClickListener(this);
        findViewById(R.id.TextViewBackToLogin).setOnClickListener(this);
        findViewById(R.id.ButtonSignUp).setOnClickListener(this);
    }

    private void sendResetPassword() {

        String email = EditTextEmailAddress.getText().toString().trim();

        //Validating the form
        if(email.isEmpty()) {
            EditTextEmailAddress.setError("Email cannot be empty.");
            EditTextEmailAddress.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EditTextEmailAddress.setError("Please enter a valid email.");
            EditTextEmailAddress.requestFocus();
            return;
        }

        ProgressDialog.setMessage("Just a minute...");
        ProgressDialog.show();

        //Set up a password reset. THIS PART IS UNFINISHED.
    }

    //Click listeners for login and sign up.
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ButtonResetPassword:
                //Sends reset password
                sendResetPassword();
                break;
            case R.id.TextViewBackToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.ButtonSignUp:
                startActivity(new Intent(this, RegisterSwitchActivity.class));
                break;
        }
    }
}
