package com.example.tp.sane.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.EntryTextGetResponse;
import com.example.tp.sane.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntryView extends AppCompatActivity {

    TextView TextViewEntryTitle, TextViewEntryText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entryview);

        TextViewEntryTitle = findViewById(R.id.TextViewEntryTitle);
        TextViewEntryText = findViewById(R.id.TextViewEntryText);

        //Gets the incoming intent
        getIncomingIntent();
    }

    private void getIncomingIntent() {
        //Checks if the incoming intent has extra variables passed
        if (getIntent().hasExtra("Title") && getIntent().hasExtra("Text")) {

            //Getting the ID
            int ID = SharedPreferencesManager.getInstance(getApplicationContext()).getUser().getID();
            final String Title = getIntent().getStringExtra("Title");

            //Calls the client
            Call<EntryTextGetResponse> call = RetrofitClient
                    .getInstance().getApi().getFullEntry(ID, Title);

            call.enqueue(new Callback<EntryTextGetResponse>() {
                @Override
                public void onResponse(Call<EntryTextGetResponse> call, Response<EntryTextGetResponse> response) {
                    EntryTextGetResponse getEntryTextResponse = response.body();
                    String Text = getEntryTextResponse.getFullEntryText();
                    //Setting the entry title and text
                    setTextViewEntryTitleText(Title, Text);
                }

                @Override
                public void onFailure(Call<EntryTextGetResponse> call, Throwable t) {

                }
            });

        }
    }

    private void setTextViewEntryTitleText(String Title, String Text) {
        TextViewEntryTitle.setText(Title);
        TextViewEntryText.setText(Text);
    }

}