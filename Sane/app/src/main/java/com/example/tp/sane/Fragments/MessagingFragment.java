package com.example.tp.sane.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Adapters.MessagingAdapter;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.DefaultResponse;
import com.example.tp.sane.Models.Message;
import com.example.tp.sane.Models.MessageGetResponse;
import com.example.tp.sane.Models.ProfileGetResponse;
import com.example.tp.sane.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingFragment extends Fragment implements View.OnClickListener {

    TextView TextViewName;
    ImageView UserImageView, ImageViewSend;
    EditText EditTextMessage;
    MessagingAdapter adapter;
    RecyclerView recyclerView;
    List<Message> messages;
    private static final String UPLOADS_URL = "http://192.168.0.48/Sane/uploads/";

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messaging, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        UserImageView = view.findViewById(R.id.UserImageView);
        TextViewName = view.findViewById(R.id.TextViewName);
        ImageViewSend = view.findViewById(R.id.ImageViewSend);
        EditTextMessage = view.findViewById(R.id.EditTextMessage);

        ImageViewSend.setOnClickListener(this);

        String Name = getArguments().getString("Name");
        int Chat_ID = Integer.parseInt(getArguments().getString("Chat_ID"));
        final int UserID = Integer.parseInt(getArguments().getString("UserID"));
        final int TherapistID = Integer.parseInt(getArguments().getString("TherapistID"));
        TextViewName.setText(Name);

        //Calls the API
        Call<MessageGetResponse> call = RetrofitClient.getInstance().getApi().getMessages(Chat_ID);

        //Waiting for response
        call.enqueue(new Callback<MessageGetResponse>() {
            @Override
            public void onResponse(Call<MessageGetResponse> call, Response<MessageGetResponse> response) {

                //stores the response
                messages = response.body().getMessages();
                //Sets the response to the adapter which handles the input
                adapter = new MessagingAdapter(getContext(), messages);

                //Sets the recyclerview with the adapter provided values
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<MessageGetResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error occurred. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
        int ID = SharedPreferencesManager.getInstance(getActivity()).getUser().getID();
        if (ID == UserID) {
            Call<ProfileGetResponse> profileGetResponseCall = RetrofitClient.getInstance().getApi().getProfile(TherapistID);

            profileGetResponseCall.enqueue(new Callback<ProfileGetResponse>() {
                @Override
                public void onResponse(Call<ProfileGetResponse> call, Response<ProfileGetResponse> response) {
                    ProfileGetResponse profileGetResponse = response.body();
                    if (profileGetResponse.getProfile().getImage_ID() != null) {
                        String url = UPLOADS_URL + TherapistID + ".jpg";
                        Picasso.get().invalidate(url);
                        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(UserImageView);
                    } else {
                        UserImageView.setImageResource(R.drawable.ic_account_circle);
                    }
                }

                @Override
                public void onFailure(Call<ProfileGetResponse> call, Throwable t) {

                }
            });

        } else {
            Call<ProfileGetResponse> profileGetResponseCall = RetrofitClient.getInstance().getApi().getProfile(UserID);

            profileGetResponseCall.enqueue(new Callback<ProfileGetResponse>() {
                @Override
                public void onResponse(Call<ProfileGetResponse> call, Response<ProfileGetResponse> response) {
                    ProfileGetResponse profileGetResponse = response.body();
                    if (profileGetResponse.getProfile().getImage_ID() != null) {
                        String url = UPLOADS_URL + UserID + ".jpg";
                        Picasso.get().invalidate(url);
                        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(UserImageView);
                    } else {
                        UserImageView.setImageResource(R.drawable.ic_account_circle);
                    }
                }

                @Override
                public void onFailure(Call<ProfileGetResponse> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ImageViewSend:
                String message = EditTextMessage.getText().toString().trim();
                int ID = SharedPreferencesManager.getInstance(getActivity()).getUser().getID();
                int Chat_ID = Integer.parseInt(getArguments().getString("Chat_ID"));
                sendMessage(ID, Chat_ID, message);
                EditTextMessage.setText("");

                //Waits one second before retrieving the message
                try {
                    TimeUnit.SECONDS.sleep(1);
                    Call<MessageGetResponse> call = RetrofitClient.getInstance().getApi().getMessages(Chat_ID);

                    call.enqueue(new Callback<MessageGetResponse>() {
                        @Override
                        public void onResponse(Call<MessageGetResponse> call, Response<MessageGetResponse> response) {
                            messages.clear();
                            //stores the response
                            messages = response.body().getMessages();
                            //Sets the response to the adapter which handles the input
                            adapter = new MessagingAdapter(getContext(), messages);

                            //Sets the recyclerview with the adapter provided values
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Call<MessageGetResponse> call, Throwable t) {

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //Sends message
    private void sendMessage(int ID, int Chat_ID, String message) {

        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().sendMessage(ID, Chat_ID, message);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse defaultResponse = response.body();
                Toast.makeText(getContext(), defaultResponse.getMsg(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {

            }
        });

    }
}
