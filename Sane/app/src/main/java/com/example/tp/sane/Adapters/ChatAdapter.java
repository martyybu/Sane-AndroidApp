package com.example.tp.sane.Adapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Fragments.MessagingFragment;
import com.example.tp.sane.Models.Chat;
import com.example.tp.sane.Models.ProfileGetResponse;
import com.example.tp.sane.Models.TherapistGetResponse;
import com.example.tp.sane.Models.UsernameGetResponse;
import com.example.tp.sane.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Chat> chats;
    private OnItemClickListener mListener;
    String UPLOADS_URL = "http://192.168.0.48/Sane/uploads/";

    //An interface for when an item is clicked
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    //sets listeners
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    //takes passed context and chats
    public ChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    //Define the holder of the data
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chatrecyclerview, null);
        ChatViewHolder holder = new ChatViewHolder(view, mListener);
        return holder;
    }

    //Bind data to the view
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        final Chat chat = chats.get(position);

        //sets hidden ID's to be passed as variables
        holder.TextViewUserID.setText(Integer.toString(chat.getUserID()));
        holder.TextViewTherapistID.setText(Integer.toString(chat.getTherapistID()));

        Call<TherapistGetResponse> call = RetrofitClient.getInstance().getApi().getTherapist(chat.getTherapistID());

        call.enqueue(new Callback<TherapistGetResponse>() {
            @Override
            public void onResponse(Call<TherapistGetResponse> call, Response<TherapistGetResponse> response) {
                TherapistGetResponse therapistGetResponse = response.body();
                //if current user is not the therapist gets the therapist image and name
                if (SharedPreferencesManager.getInstance(context).getUser().getID() != therapistGetResponse.getTherapist().getID()) {
                    holder.TextViewName.setText(therapistGetResponse.getTherapist().getName() + " " + therapistGetResponse.getTherapist().getSurname());
                    String url = UPLOADS_URL + therapistGetResponse.getTherapist().getID() + ".jpg";
                    Picasso.get().invalidate(url);
                    Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.UserImageView);
                }
                //if current user is the therapist
                else {
                    Call<UsernameGetResponse> usernameGetResponseCall = RetrofitClient.getInstance().getApi().getUserUsername(chat.getUserID());

                    usernameGetResponseCall.enqueue(new Callback<UsernameGetResponse>() {
                        @Override
                        public void onResponse(Call<UsernameGetResponse> call, Response<UsernameGetResponse> response) {
                            UsernameGetResponse usernameGetResponse = response.body();
                            //Gets the username of the user for the therapist
                            String username = usernameGetResponse.getUsername();
                            holder.TextViewName.setText(username);
                        }

                        @Override
                        public void onFailure(Call<UsernameGetResponse> call, Throwable t) {

                        }
                    });

                    Call<ProfileGetResponse> profileGetResponseCall = RetrofitClient.getInstance().getApi().getProfile(chat.getUserID());

                    profileGetResponseCall.enqueue(new Callback<ProfileGetResponse>() {
                        @Override
                        public void onResponse(Call<ProfileGetResponse> call, Response<ProfileGetResponse> response) {
                            ProfileGetResponse profileGetResponse = response.body();
                            //Uses image if there is one
                            if (profileGetResponse.getProfile().getImage_ID() != null) {
                                String url = UPLOADS_URL + chat.getUserID() + ".jpg";
                                Picasso.get().invalidate(url);
                                Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.UserImageView);
                            } else {
                                holder.UserImageView.setImageResource(R.drawable.ic_account_circle);
                            }
                        }

                        @Override
                        public void onFailure(Call<ProfileGetResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<TherapistGetResponse> call, Throwable t) {

            }
        });
        //Sets hidden ID's to be passed as variables
        if (chat.getChat_ID() != 0) {
            String Chat_ID = Integer.toString(chat.getChat_ID());
            holder.TextViewLastMessage.setText(Chat_ID);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView TextViewName, TextViewLastMessage, TextViewUserID, TextViewTherapistID;
        ImageView UserImageView;

        public ChatViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            TextViewName = itemView.findViewById(R.id.TextViewName);
            TextViewLastMessage = itemView.findViewById(R.id.TextViewLastMessage);
            UserImageView = itemView.findViewById(R.id.UserImageView);
            TextViewUserID = itemView.findViewById(R.id.TextViewUserID);
            TextViewTherapistID = itemView.findViewById(R.id.TextViewTherapistID);


            //Setting click listener for each item in search
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            //Put the value

                            MessagingFragment messagingFragment = new MessagingFragment();

                            //Passes extra arguments to the next fragment
                            Bundle args = new Bundle();
                            args.putString("Name", TextViewName.getText().toString().trim());
                            args.putString("Chat_ID", TextViewLastMessage.getText().toString().trim());
                            args.putString("UserID", TextViewUserID.getText().toString().trim());
                            args.putString("TherapistID", TextViewTherapistID.getText().toString().trim());
                            messagingFragment.setArguments(args);

                            FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();

                            manager.beginTransaction().replace(R.id.RL_Container, messagingFragment).commit();
                        }

                    }
                }
            });
        }

    }
}