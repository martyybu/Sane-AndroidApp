package com.example.tp.sane.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Adapters.EntriesAdapter;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.Entry;
import com.example.tp.sane.Models.EntryResponse;
import com.example.tp.sane.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryFragment extends Fragment {

    RecyclerView entryRecyclerView;

    EntriesAdapter adapter;

    List<Entry> entriesList;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Makes sure that it has an option menu
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }


    //When an item is clicked ...
    public void openEntry(int position) {
        entriesList.get(position);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        entryRecyclerView = view.findViewById(R.id.RecyclerView);
        entryRecyclerView.setHasFixedSize(true);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        int ID = SharedPreferencesManager.getInstance(getActivity()).getUser().getID();
        //Calls the API
        Call<EntryResponse> call = RetrofitClient.getInstance().getApi().getEntries(ID);

        //Waiting for response
        call.enqueue(new Callback<EntryResponse>() {
            @Override
            public void onResponse(Call<EntryResponse> call, Response<EntryResponse> response) {

                //stores the response
                entriesList = response.body().getEntries();

                //Sets the response to the adapter which handles the input
                adapter = new EntriesAdapter(getContext(), entriesList);

                //Sets the recyclerview with the adapter provided values
                entryRecyclerView.setAdapter(adapter);

                //Sets click listener on each item
                adapter.setOnItemClickListener(new EntriesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        openEntry(position);
                    }
                });
            }
            @Override
            public void onFailure(Call<EntryResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error occurred. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Sets up options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.diary_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Sets up the listeners for options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        fragment = new EntryFragment();
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
