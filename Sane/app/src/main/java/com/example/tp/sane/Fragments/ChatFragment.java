package com.example.tp.sane.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Adapters.ChatAdapter;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.Chat;
import com.example.tp.sane.Models.ChatGetResponse;
import com.example.tp.sane.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    RecyclerView chatrecyclerview;

    ChatAdapter adapter;

    List<Chat> chats;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    //When an item is clicked ...
    public void openChat(int position) {
        chats.get(position);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatrecyclerview = view.findViewById(R.id.RecyclerView);
        chatrecyclerview.setHasFixedSize(true);
        chatrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        int ID = SharedPreferencesManager.getInstance(getActivity()).getUser().getID();
        //Calls the API
        Call<ChatGetResponse> call = RetrofitClient.getInstance().getApi().getChats(ID);

        //Waiting for response
        call.enqueue(new Callback<ChatGetResponse>() {
            @Override
            public void onResponse(Call<ChatGetResponse> call, Response<ChatGetResponse> response) {

                //stores the response
                chats = response.body().getChats();

                //Sets the response to the adapter which handles the input
                adapter = new ChatAdapter(getContext(), chats);

                //Sets the recyclerview with the adapter provided values
                chatrecyclerview.setAdapter(adapter);

                //Sets click listener on each item
                adapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        openChat(position);
                    }
                });
            }
            @Override
            public void onFailure(Call<ChatGetResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error occurred. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;

        fragment = new MessagingFragment();
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
}
