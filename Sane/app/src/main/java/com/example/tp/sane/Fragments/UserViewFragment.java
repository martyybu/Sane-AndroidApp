package com.example.tp.sane.Fragments;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.DefaultResponse;
import com.example.tp.sane.Models.Profile;
import com.example.tp.sane.Models.ProfileGetResponse;
import com.example.tp.sane.Models.Therapist;
import com.example.tp.sane.Models.TherapistGetResponse;
import com.example.tp.sane.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewFragment extends Fragment implements View.OnClickListener {

    private static final String UPLOADS_URL = "http://192.168.0.48/Sane/uploads/";
    private TextView TextViewName, TextViewDoB, TextViewVerifiedValue, TextViewVerifiedText, TextViewBio, TextViewDiscipline;
    ImageView UserImageView;
    Therapist therapist;
    Profile profile;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextViewName = view.findViewById(R.id.TextViewName);
        TextViewDoB = view.findViewById(R.id.TextViewDoB);
        TextViewVerifiedValue = view.findViewById(R.id.TextViewVerifiedValue);
        TextViewVerifiedText = view.findViewById(R.id.TextViewVerifiedText);
        UserImageView = view.findViewById(R.id.UserImageView);
        TextViewBio = view.findViewById(R.id.TextViewBio);
        TextViewDiscipline = view.findViewById(R.id.TextViewDiscipline);

        view.findViewById(R.id.MessageButton).setOnClickListener(this);

        //Gets the incoming intent
        getIncomingIntent();

    }

    private void getIncomingIntent() {
        String parsedID = getArguments().getString("ID");
        final int ID = Integer.parseInt(parsedID);
        //Calls the client
        Call<TherapistGetResponse> call = RetrofitClient
                .getInstance().getApi().getTherapist(ID);

        call.enqueue(new Callback<TherapistGetResponse>() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<TherapistGetResponse> call, Response<TherapistGetResponse> response) {
                therapist = response.body().getTherapist();
                String name = therapist.getName() + " " + therapist.getSurname();
                String DoB = therapist.getDoB();
                String discipline = therapist.getDiscipline();

                //Gets age from date
                LocalDate today = LocalDate.now();                          //Today's date
                LocalDate birthday = LocalDate.parse(DoB);
                int age = Period.between(birthday, today).getYears();

                TextViewName.setText(name);
                TextViewDoB.setText("Age:" + age);
                TextViewDiscipline.setText(discipline);
                TextViewVerifiedText.setText("Verified: ");
                if (therapist.getValid() == 0) {
                    TextViewVerifiedValue.setTextColor(Color.parseColor("#FF0000"));
                    TextViewVerifiedValue.setText("FALSE");
                } else if (therapist.getValid() == 1) {
                    TextViewVerifiedValue.setTextColor(Color.parseColor("#008000"));
                    TextViewVerifiedValue.setText("TRUE");
                }

            }

            @Override
            public void onFailure(Call<TherapistGetResponse> call, Throwable t) {

            }
        });

        Call<ProfileGetResponse> profileGetResponseCall = RetrofitClient.getInstance().getApi().getProfile(ID);

        profileGetResponseCall.enqueue(new Callback<ProfileGetResponse>() {
            @Override
            public void onResponse(Call<ProfileGetResponse> call, Response<ProfileGetResponse> response) {
                if (response.body().getProfile() != null) {
                    profile = response.body().getProfile();

                    if (profile.getImage_ID() != null) {
                        String url = UPLOADS_URL + ID + ".jpg";
                        Picasso.get().invalidate(url);
                        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(UserImageView);
                    } else {
                        UserImageView.setImageResource(R.drawable.ic_account_circle);
                    }
                    TextViewBio.setText(profile.getAbout());
                }
            }

            @Override
            public void onFailure(Call<ProfileGetResponse> call, Throwable t) {

            }
        });
    }

    //Sets up click listeners
    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.MessageButton:
                try {
                    fragment = new ChatFragment();
                    String parsedID = getArguments().getString("ID");
                    final int TherapistID = Integer.parseInt(parsedID);
                    final int UserID = SharedPreferencesManager.getInstance(getActivity()).getUser().getID();
                    createChat(UserID, TherapistID);
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }

        if (fragment != null) {
            displayFragment(fragment);
        }
    }


    //Creates the chat
    private void createChat(int UserID, int TherapistID) {
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().createChat(UserID, TherapistID);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
            }
        });
    }

    //Displays the fragment on call
    private void displayFragment(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.RL_Container, fragment)
                .commit();
    }
}


