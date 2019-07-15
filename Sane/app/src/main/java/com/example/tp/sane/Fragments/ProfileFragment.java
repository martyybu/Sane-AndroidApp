package com.example.tp.sane.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Activities.LoginActivity;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.Profile;
import com.example.tp.sane.Models.ProfileGetResponse;
import com.example.tp.sane.Models.Therapist;
import com.example.tp.sane.Models.TherapistGetResponse;
import com.example.tp.sane.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String UPLOADS_URL = "http://192.168.0.48/Sane/uploads/";
    private TextView TextViewUsername, TextViewEmail, TextViewName, TextViewDoB, TextViewVerifiedValue, TextViewVerifiedText, TextViewBio;
    ImageView UserImageView;
    Therapist therapist;
    Profile profile;

    //When the view is created, opens the profile fragment
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    //Sets the top right options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Logging out of the user
    private void logout() {
        SharedPreferencesManager.getInstance(getActivity()).clear();
        Intent LoginActivity = new Intent(getActivity(), LoginActivity.class);
        LoginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginActivity);
    }

    //Top right select options in profile view
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.UpdateProfile:
                fragment = new UpdateProfileFragment();
                break;
            case R.id.Logout:
                logout();
                break;
        }

        if (fragment != null) {
            displayFragment(fragment);
        }
        return true;
    }

    //Displays the fragment on call
    private void displayFragment(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.RL_Container, fragment)
                .commit();
    }

    //Loads the values when the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextViewUsername = view.findViewById(R.id.TextViewUsername);
        TextViewEmail = view.findViewById(R.id.TextViewEmail);
        TextViewName = view.findViewById(R.id.TextViewName);
        TextViewDoB = view.findViewById(R.id.TextViewDoB);
        TextViewVerifiedValue = view.findViewById(R.id.TextViewVerifiedValue);
        TextViewVerifiedText = view.findViewById(R.id.TextViewVerifiedText);
        UserImageView = view.findViewById(R.id.UserImageView);
        TextViewBio = view.findViewById(R.id.TextViewBio);

        final int ID = SharedPreferencesManager.getInstance(getActivity()).getUser().getID();

        TextViewUsername.setText(SharedPreferencesManager.getInstance(getActivity()).getUser().getUsername());
        TextViewEmail.setText(SharedPreferencesManager.getInstance(getActivity()).getUser().getEmail());

        //Calls the API to get therapist info
        Call<TherapistGetResponse> call = RetrofitClient.getInstance().getApi().getTherapist(ID);

        call.enqueue(new Callback<TherapistGetResponse>() {
            @Override
            public void onResponse(Call<TherapistGetResponse> call, Response<TherapistGetResponse> response) {

                //stores the response. Checks if it's a therapist or not
                if (response.body().getTherapist() != null) {
                    therapist = response.body().getTherapist();

                    TextViewName.setText(therapist.getName() + " " + therapist.getSurname());
                    TextViewDoB.setText(therapist.getDoB());
                    TextViewVerifiedText.setText("Verified: ");
                    if (therapist.getValid() == 0) {
                        TextViewVerifiedValue.setTextColor(Color.parseColor("#FF0000"));
                        TextViewVerifiedValue.setText("FALSE");
                    } else if (therapist.getValid() == 1) {
                        TextViewVerifiedValue.setTextColor(Color.parseColor("#008000"));
                        TextViewVerifiedValue.setText("TRUE");
                    }
                }
                return;
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
                    //If user image exists, loads it
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

}
