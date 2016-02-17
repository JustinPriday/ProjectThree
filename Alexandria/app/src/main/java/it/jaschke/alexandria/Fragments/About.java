package it.jaschke.alexandria.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.R;


public class About extends Fragment {

    public About(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ActionBar aBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (aBar != null) {
            aBar.setDisplayHomeAsUpEnabled(true);
            aBar.setDisplayShowHomeEnabled(true);
            aBar.setTitle(getResources().getString(R.string.about));
        }
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.about);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
