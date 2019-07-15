package com.example.tp.sane.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Adapters.TherapistsAdapter;
import com.example.tp.sane.Models.Therapist;
import com.example.tp.sane.Models.TherapistResponse;
import com.example.tp.sane.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener{

    RecyclerView recyclerView;
    TherapistsAdapter adapter;

    List<Therapist> therapistList;

    //Inflates the fragment
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    //When an item is clicked ...
    public void changeItem(int position, String text) {
        therapistList.get(position).changeText1(text);
        adapter.notifyItemChanged(position);
    }

    //When the view is created sets the recyclerview
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Calls the API
        Call<TherapistResponse> call = RetrofitClient.getInstance().getApi().getTherapists();

        //Waiting for response
        call.enqueue(new Callback<TherapistResponse>() {
            @Override
            public void onResponse(Call<TherapistResponse> call, Response<TherapistResponse> response) {

                //stores the response
                therapistList = response.body().getTherapists();
                //Sets the response to the adapter which handles the input
                adapter = new TherapistsAdapter(getContext(), therapistList);

                //Sets up the filter on the items
                List<Therapist> newList = new ArrayList<>();
                for (Therapist therapist : therapistList) {
                    newList.add(therapist);
                    adapter.setFilter(newList);
                }

                //Sets the recyclerview with the adapter provided values
                recyclerView.setAdapter(adapter);

                //Sets click listener on items in the recyclerview
                adapter.setOnItemClickListener(new TherapistsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        changeItem(position, "Clicked");
                    }
                });
            }
            @Override
            public void onFailure(Call<TherapistResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error occurred. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    //When the text changes, so does the list
    @Override
    public boolean onQueryTextChange(String s) {

        s = s.toLowerCase();
        List<Therapist> newList = new ArrayList<>();
        //Changes the list of therapists in therapist search according to input(by name)
        if (therapistList != null) {
            for (Therapist therapist : therapistList) {
                String name = therapist.getName().toLowerCase();
                if (name.contains(s)) {
                    newList.add(therapist);
                }
            }

            adapter.setFilter(newList);
        }
        return true;
    }
}
