package com.example.tp.sane.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.Message;
import com.example.tp.sane.R;

import java.util.List;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.MessagingViewHolder> {

    private Context context;
    private List<Message> messages;
    private MessagingAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MessagingAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void setOnItemClickListener(MessagingAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    //Define the holder of the data
    @NonNull
    @Override
    public MessagingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.messagingrecyclerview, null);
        MessagingViewHolder holder = new MessagingViewHolder(view, mListener);
        return holder;
    }

    //Bind data to the view
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MessagingViewHolder holder, final int position) {
        final Message message = messages.get(position);

        //Gets the current therapist or user messages
        if (SharedPreferencesManager.getInstance(context).getUser().getID() == message.getID()) {
            holder.TextViewChat.setText("You: " + message.getMessage());
            holder.LinearLayoutChat.setBackgroundColor(Color.WHITE);
        }
        // Gets the current user or therapist messages
        else {
            holder.TextViewChat.setText(message.getMessage());
            holder.LinearLayoutChat.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessagingViewHolder extends RecyclerView.ViewHolder {

        TextView TextViewEntryTitle, TextViewEntryText, TextViewChat;
        RelativeLayout parentLayout;
        LinearLayout LinearLayoutChat;

        public MessagingViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            TextViewEntryTitle = itemView.findViewById(R.id.TextViewEntryTitle);
            TextViewChat = itemView.findViewById(R.id.TextViewChat);
            TextViewEntryText = itemView.findViewById(R.id.TextViewEntryText);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            LinearLayoutChat = itemView.findViewById(R.id.LinearLayoutChat);

            //Setting click listener for each item in search
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }

                    }
                }
            });
        }

    }
}