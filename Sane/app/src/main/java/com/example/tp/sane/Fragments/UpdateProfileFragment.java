package com.example.tp.sane.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.DefaultResponse;
import com.example.tp.sane.Models.Profile;
import com.example.tp.sane.Models.ProfileGetResponse;
import com.example.tp.sane.Models.Therapist;
import com.example.tp.sane.Models.TherapistGetResponse;
import com.example.tp.sane.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class UpdateProfileFragment extends Fragment implements View.OnClickListener {

    private static final String UPLOADS_URL = "http://192.168.0.48/Sane/uploads/";
    private TextView TextViewUsername, TextViewEmail, TextViewName, TextViewDoB, TextViewVerifiedValue, TextViewVerifiedText;
    EditText EditTextAbout;
    Button UploadImageButton, SaveButton;
    ImageView UserImageView;
    Therapist therapist;
    Profile profile;
    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_updateprofile, container, false);
    }

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
        UploadImageButton = view.findViewById(R.id.UploadImageButton);
        SaveButton = view.findViewById(R.id.SaveButton);
        EditTextAbout = view.findViewById(R.id.EditTextAbout);

        UploadImageButton.setOnClickListener(this);
        SaveButton.setOnClickListener(this);

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
                        Picasso.get().load(UPLOADS_URL + ID + ".jpg").into(UserImageView);
                    } else {
                        UserImageView.setImageResource(R.drawable.ic_account_circle);
                    }
                    EditTextAbout.setText(profile.getAbout());
                }
            }
            @Override
            public void onFailure(Call<ProfileGetResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.UploadImageButton:
                selectImage();
                break;
            case R.id.SaveButton:
                updateProfile();
                //startActivity(new Intent(getContext(), ProfileFragment.class));
                break;
        }
    }

    //Updates the profile
    private void updateProfile() {
        String image = imageToString();
        int ID = SharedPreferencesManager.getInstance(getActivity()).getUser().getID();
        String about = EditTextAbout.getText().toString().trim();
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().updateProfile(ID, image, about);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse DefaultResponse = response.body();
                Toast.makeText(getContext(), DefaultResponse.getMsg(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getContext(),"Error.", Toast.LENGTH_LONG).show();
            }
        });

    }

    //Selects the current image
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    //Gets the image from the device
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data != null) {

            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path);
                UserImageView.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Converts an image to a string
    private String imageToString() {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imgByte = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imgByte, Base64.DEFAULT);
        }
        return null;
    }
}