package com.example.tp.sane.Fragments;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.tp.sane.API.RetrofitClient;
import com.example.tp.sane.Data.SharedPreferencesManager;
import com.example.tp.sane.Models.DefaultResponse;
import com.example.tp.sane.R;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntryFragment extends Fragment {

    EditText EditTextEntryTitle, EditTextEntryText;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        EditTextEntryTitle = view.findViewById(R.id.EditTextEntryTitle);
        EditTextEntryText = view.findViewById(R.id.EditTextEntryText);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.entry_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.save:
                addEntry();
                try {
                    TimeUnit.SECONDS.sleep(1);
                    fragment = new DiaryFragment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    //Adds an entry using ID, title, text
    private void addEntry() {
        int ID = SharedPreferencesManager.getInstance(getContext()).getUser().getID();
        String title = EditTextEntryTitle.getText().toString().trim();
        String text = EditTextEntryText.getText().toString().trim();

        if (title.isEmpty()) {
            title = "Title";
        }
        if (text.isEmpty()) {
            text = "Text";
        }
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().addEntry(ID, title, text);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse DefaultResponse = response.body();
                Toast.makeText(getContext(), DefaultResponse.getMsg(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {

            }
        });

    }
}
