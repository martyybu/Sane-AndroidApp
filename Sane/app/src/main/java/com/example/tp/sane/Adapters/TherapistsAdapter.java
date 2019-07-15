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
import com.example.tp.sane.Fragments.UserViewFragment;
import com.example.tp.sane.Models.Profile;
import com.example.tp.sane.Models.ProfileGetResponse;
import com.example.tp.sane.Models.Therapist;
import com.example.tp.sane.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TherapistsAdapter extends RecyclerView.Adapter<TherapistsAdapter.TherapistsViewHolder> {

    private Context context;
    private List<Therapist> therapistList;
    private OnItemClickListener mListener;
    Profile profile;
    String UPLOADS_URL = "http://192.168.0.48/Sane/uploads/";

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    public TherapistsAdapter(Context context, List<Therapist> therapistList) {
        this.context = context;
        this.therapistList = therapistList;
    }

    //Define the holder of the data
    @NonNull
    @Override
    public TherapistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview, null);
        TherapistsViewHolder holder = new TherapistsViewHolder(view, mListener);
        return holder;
    }

    //Bind data to the view
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final TherapistsViewHolder holder, int position) {
        final Therapist therapist = therapistList.get(position);

        holder.TextViewName.setText(therapist.getName() + " " + therapist.getSurname());

        //Gets age from date
        LocalDate today = LocalDate.now();                          //Today's date
        LocalDate birthday = LocalDate.parse(therapist.getDoB());
        int age = Period.between(birthday, today).getYears();
        int valid = therapist.getValid();
        final int ID = therapist.getID();

        holder.TextViewAge.setText("Age: " + age);
        holder.TextViewDiscipline.setText("Discipline: " + therapist.getDiscipline());
        holder.TextViewID.setText(Integer.toString(ID));
        if (valid == 1) {
            holder.TherapistCheck.setImageResource(R.drawable.ic_check);
        }
        Call<ProfileGetResponse> profileGetResponseCall = RetrofitClient.getInstance().getApi().getProfile(ID);

        profileGetResponseCall.enqueue(new Callback<ProfileGetResponse>() {
            @Override
            public void onResponse(Call<ProfileGetResponse> call, Response<ProfileGetResponse> response) {
                if (response.body().getProfile() != null) {
                    profile = response.body().getProfile();

                    if (profile.getImage_ID() != null) {
                        String url = UPLOADS_URL + ID + ".jpg";
                        Picasso.get().invalidate(url);
                        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.TherapistImageView);
                    } else {
                        holder.TherapistImageView.setImageResource(R.drawable.ic_account_circle);
                    }
                }
            }
            @Override
            public void onFailure(Call<ProfileGetResponse> call, Throwable t) {
            }
        });


    }

    @Override
    public int getItemCount() {
        return therapistList.size();
    }

    //Sets filter for the items when a value is being searched
    public void setFilter (List<Therapist> newList) {
        therapistList = new ArrayList<>();
        therapistList.addAll(newList);
        notifyDataSetChanged();
    }

    class TherapistsViewHolder extends RecyclerView.ViewHolder {

        TextView TextViewName, TextViewAge, TextViewDiscipline, TextViewID;
        ImageView TherapistImageView, TherapistCheck;

        public TherapistsViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            TextViewName = itemView.findViewById(R.id.TextViewName);
            TextViewAge = itemView.findViewById(R.id.TextViewAge);
            TextViewDiscipline = itemView.findViewById(R.id.TextViewDiscipline);
            TherapistImageView = itemView.findViewById(R.id.TherapistImageView);
            TherapistCheck = itemView.findViewById(R.id.TherapistCheck);
            TextViewID = itemView.findViewById(R.id.TextViewID);

            //Setting click listener for each item in search
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            //Put the value
                            UserViewFragment userViewFragment = new UserViewFragment();

                            Bundle args = new Bundle();
                            args.putString("ID", (String) TextViewID.getText());
                            userViewFragment.setArguments(args);

                            FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();

                            manager.beginTransaction().replace(R.id.RL_Container, userViewFragment).commit();
                        }

                    }
                }
            });
        }

    }
}