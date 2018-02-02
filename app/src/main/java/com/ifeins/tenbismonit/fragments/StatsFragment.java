package com.ifeins.tenbismonit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifeins.tenbismonit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsFragment extends Fragment implements HomeAdapterFragment {


    public StatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void subscribeForUpdates() {

    }

    @Override
    public void unsubscribeForUpdates() {

    }
}
