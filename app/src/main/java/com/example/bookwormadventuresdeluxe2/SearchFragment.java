package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;

/**
 * A {@link Fragment} subclass for navbar menu item 3
 */
public class SearchFragment extends Fragment
{

    MaterialTextView appHeaderText;
    SearchView searchView;

    public SearchFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        /* Set title */
        appHeaderText = view.findViewById(R.id.app_header_title);
        appHeaderText.setText(R.string.search_title);

        // https://stackoverflow.com/questions/17670685/custom-searchview-whole-clickable-in-android/47826388
        searchView = (SearchView) view.findViewById(R.id.search_bar);
        searchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchView.setIconified(false);
            }
        });
        return view;
    }
}