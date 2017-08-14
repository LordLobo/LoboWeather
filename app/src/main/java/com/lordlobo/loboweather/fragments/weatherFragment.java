package com.lordlobo.loboweather.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lordlobo.loboweather.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class weatherFragment extends Fragment {

    String city = "";

    public weatherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        city = getActivity().getIntent().getStringExtra("cityName");

        TextView text = rootView.findViewById(R.id.helloText);

        text.setText(city);

        return rootView;
    }
}
