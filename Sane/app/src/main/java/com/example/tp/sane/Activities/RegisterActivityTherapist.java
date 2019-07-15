package com.example.tp.sane.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Models.DefaultResponse;
import com.example.tp.sane.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivityTherapist extends AppCompatActivity implements View.OnClickListener {

    EditText EditTextDateOfBirth, EditTextName, EditTextSurname, EditTextEmailAddress, EditTextUsername, EditTextPassword, EditTextDiscipline, EditTextPhoneNumber;
    DatePickerDialog DatePickerDialog;
    private ProgressDialog ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_therapist);

        EditTextDateOfBirth = findViewById(R.id.EditTextDateOfBirth);
        EditTextName = findViewById(R.id.EditTextName);
        EditTextSurname = findViewById(R.id.EditTextSurname);
        EditTextEmailAddress = findViewById(R.id.EditTextEmailAddress);
        EditTextUsername = findViewById(R.id.EditTextUsername);
        EditTextPassword = findViewById(R.id.EditTextPassword);
        EditTextPhoneNumber = findViewById(R.id.EditTextPhoneNumber);
        EditTextDiscipline = findViewById(R.id.EditTextDiscipline);

        ProgressDialog = new ProgressDialog(this);

        findViewById(R.id.ButtonSignUp).setOnClickListener(this);
        findViewById(R.id.TextViewBackToLogin).setOnClickListener(this);

        //Click listener for date of birth edit text
        EditTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                int mYear = currentDate.get(Calendar.YEAR); // current year
                int mMonth = currentDate.get(Calendar.MONTH); // current month
                int mDay = currentDate.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog = new DatePickerDialog(RegisterActivityTherapist.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month, month and year value in the EditText
                                EditTextDateOfBirth.setText(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear-18, mMonth, mDay);
                currentDate.set(mYear-18,mMonth,mDay);
                int minYear=mYear-18, maxYear=mYear-100, month=mMonth, day=mDay; // setting minimum and maximum dates
                Calendar minCal = Calendar.getInstance();
                minCal.set( minYear, month, day, 0, 0, 0 );
                DatePickerDialog.getDatePicker().setMaxDate(minCal.getTimeInMillis());
                Calendar maxCal = Calendar.getInstance();
                maxCal.set( maxYear, month, day, 0, 0, 0 );
                DatePickerDialog.getDatePicker().setMinDate(maxCal.getTimeInMillis());
                DatePickerDialog.show();
                EditTextDateOfBirth.setError(null);//removes error
                EditTextDateOfBirth.clearFocus();    //clear focus from edittext
            }
        });
    }

    //Registering the therapist
    private void therapistSignUp(){
        //Taking the values from the UI (for user)
        String username = EditTextUsername.getText().toString().trim();
        String email = EditTextEmailAddress.getText().toString().trim();
        String password = EditTextPassword.getText().toString().trim();

        //Taking values from the UI (for therapist)
        String discipline = EditTextDiscipline.getText().toString().trim();
        String phoneNumber = EditTextPhoneNumber.getText().toString().trim();
        String DoB = EditTextDateOfBirth.getText().toString().trim();
        String name = EditTextName.getText().toString().trim();
        String surname = EditTextSurname.getText().toString().trim();

        //Setting the therapist to invalid(0) until it is validated by an administrator(to 1)
        final int valid = 0;

        //Validating the form
        if(name.isEmpty()) {
            EditTextName.setError("Please insert your name.");
            EditTextName.requestFocus();
            return;
        }
        if(surname.isEmpty()) {
            EditTextSurname.setError("Please insert your surname.");
            EditTextSurname.requestFocus();
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
        if(DoB == null) {
            EditTextDateOfBirth.setError("Please provide a date of birth.");
            EditTextDateOfBirth.requestFocus();
            return;
        }
        if(phoneNumber.length() != 11) {
            EditTextPhoneNumber.setError("Please provide a correct phone number. (07...)");
            EditTextPhoneNumber.requestFocus();
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
        if(discipline.isEmpty()) {
            discipline = "All"; //Default value if no discipline is written. This can be changed by the admin when verifying the therapist.
        }
        ProgressDialog.setMessage("Registering Therapist...");
        ProgressDialog.show();

        //Calling the API
        Call<DefaultResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .createTherapist(username, password, email, name, surname, DoB, phoneNumber, discipline, valid);

        //Waiting for response
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                ProgressDialog.dismiss();
                //Reading the response codes and acting accordingly
                if(response.code() == 201) {
                    DefaultResponse dr = response.body(); //Storing the response
                    Toast.makeText(RegisterActivityTherapist.this, dr.getMsg(), Toast.LENGTH_LONG).show();
                    //Wait 1 second before redirecting to login view
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        startActivity(new Intent(RegisterActivityTherapist.this, LoginActivity.class));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (response.code() == 422) {
                    Toast.makeText(RegisterActivityTherapist.this, "User with this username or email already exist.", Toast.LENGTH_LONG).show();
                } else if (response.code() == 423) {
                    Toast.makeText(RegisterActivityTherapist.this, "Error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                ProgressDialog.dismiss();
                Toast.makeText(RegisterActivityTherapist.this, "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ButtonSignUp:
                therapistSignUp();
                break;
            case R.id.TextViewBackToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
