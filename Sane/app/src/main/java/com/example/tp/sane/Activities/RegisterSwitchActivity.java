package com.example.tp.sane.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.tp.sane.R;

public class RegisterSwitchActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_switch);

        findViewById(R.id.ButtonRegisterAsAUser).setOnClickListener(this);
        findViewById(R.id.ButtonRegisterAsATherapist).setOnClickListener(this);
        findViewById(R.id.TextViewBackToLogin).setOnClickListener(this);
    }

    //Listener for switch activity, to see if registration is being made for a user or a therapist
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ButtonRegisterAsAUser:
                startActivity(new Intent(this, RegisterActivityUser.class));
                break;
            case R.id.ButtonRegisterAsATherapist:
                startActivity(new Intent(this, RegisterActivityTherapist.class));
                break;
            case R.id.TextViewBackToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

}
