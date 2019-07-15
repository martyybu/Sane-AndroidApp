package com.example.tp.sane.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tp.sane.Activities.EntryView;
import com.example.tp.sane.Models.Entry;
import com.example.tp.sane.R;

import java.util.List;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.EntriesViewHolder> {

    private Context context;
    private List<Entry> entries;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    public EntriesAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entries = entries;
    }

    //Define the holder of the data
    @NonNull
    @Override
    public EntriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.entryrecyclerview, null);
        EntriesViewHolder holder = new EntriesViewHolder(view, mListener);
        return holder;
    }

    //Bind data to the view
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final EntriesViewHolder holder, final int position) {
        final Entry entry = entries.get(position);

        holder.TextViewEntryTitle.setText(entry.getEntryTitle());
        holder.TextViewEntryText.setText(entry.getEntryText());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    class EntriesViewHolder extends RecyclerView.ViewHolder {

        TextView TextViewEntryTitle, TextViewEntryText;
        RelativeLayout parentLayout;

        public EntriesViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            TextViewEntryTitle = itemView.findViewById(R.id.TextViewEntryTitle);
            TextViewEntryText = itemView.findViewById(R.id.TextViewEntryText);
            parentLayout = itemView.findViewById(R.id.parentLayout);

            //Setting click listener for each item in search
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            Intent intent = new Intent(context, EntryView.class);
                            intent.putExtra("Title", TextViewEntryTitle.getText().toString().trim());
                            intent.putExtra("Text", TextViewEntryText.getText().toString().trim());
                            context.startActivity(intent);
                        }

                    }
                }
            });
        }

    }
}