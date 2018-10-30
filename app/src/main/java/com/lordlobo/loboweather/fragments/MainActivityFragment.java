package com.lordlobo.loboweather.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lordlobo.loboweather.R;
import com.lordlobo.loboweather.activities.WeatherActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivityFragment extends Fragment {

    EditText zipText;
    Button goButton;
    ListView visitedList;
    TextView statusText;

    ArrayList<String> visitedCities;
    SharedPreferences prefs;
    static final String visitedKeyName = "visitedCities";
    ArrayAdapter<String> visitedListAdapter;

    public MainActivityFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        zipText = rootView.findViewById(R.id.zipText);
        goButton = rootView.findViewById(R.id.goButton);
        visitedList = rootView.findViewById(R.id.visitedList);
        statusText = rootView.findViewById(R.id.statusText);

        prefs = Objects.requireNonNull(this.getActivity()).getPreferences(Activity.MODE_PRIVATE);
        visitedCities = new ArrayList<>(Arrays.asList(prefs.getString(visitedKeyName, "").split(",")));

        visitedListAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.listview_visitedcitiescell, visitedCities);
        visitedList.setAdapter(visitedListAdapter);

        visitedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String cityName = visitedCities.get(position);

                goToCity(cityName);
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = zipText.getText().toString();

                if (city.length() > 0) {
                    // could validate here

                    // Save the location to the list
                    visitedCities.add(city);
                    prefs.edit().putString(visitedKeyName, TextUtils.join(",", visitedCities))
                                .apply();

                    goToCity(city);
                } else {
                    statusText.setText("Please enter a city name or zip code");
                    statusText.setTextColor(Color.RED);
                }
            }
        });

        return rootView;
    }

    private void goToCity(String cityName) {
        Intent weatherPage =  new Intent(this.getContext(), WeatherActivity.class);
        weatherPage.putExtra("cityName", cityName);
        startActivity(weatherPage);
    }
}
