package com.lordlobo.loboweather.fragments;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lordlobo.loboweather.R;
import com.lordlobo.loboweather.databinding.FragmentWeatherBinding;
import com.lordlobo.loboweather.models.CurrentTemp;
import com.lordlobo.loboweather.networking.WeatherNetworking;
import com.lordlobo.loboweather.receivers.VibrateReceiver;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Button bcButton;

    Handler handler;

    String city;

    String uom;

    FragmentWeatherBinding binding;
    CurrentTemp myCurrentTemp;

    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false);
        View rootView = binding.getRoot();

        cityField = rootView.findViewById(R.id.city_field);
        updatedField = rootView.findViewById(R.id.updated_field);
        detailsField = rootView.findViewById(R.id.details_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        updateWeatherData(city);

        // note that this will refresh the view every 15m,
        // however the weather data may not be refreshed
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                Log.d("Refresh", "Weather Refresthed for " + city);
                updateWeatherData(city);
                handler.postDelayed(this, 60 * 15000);
            }
        }, 60 * 15000);

        bcButton = rootView.findViewById(R.id.bcButton);

        bcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VibrateReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 234324243, intent, 0);
                AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000), pendingIntent);
                Toast.makeText(getActivity(), "will vibrate in 10 secs", Toast.LENGTH_LONG).show();
            }
        });

        // databinding
        myCurrentTemp = new CurrentTemp();
        binding.setCurTemp(myCurrentTemp);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weathericons.ttf");

        city = getActivity().getIntent().getStringExtra("cityName");
    }

    @Override
    public void onResume() {
        super.onResume();

        // this kinda 'fakes' a listener on prefs but accomplishes the same effect
        uom = prefUnits();

        updateWeatherData(city);
    }

    private void updateWeatherData(final String selectedCity) {

        city = selectedCity;

        // this could be architect'd better (i.e. out of the 'controller') but isn't terribly relevant outside of using more libraries
        new Thread() {
            public void run() {
                String units = prefUnits();

                final JSONObject json = WeatherNetworking.getJSON(getActivity(), selectedCity, units);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private String prefUnits() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        return prefs.getString(getString(R.string.unitOfMeasureKey), "imperial");
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            String tempSuffix = " F";

            if (uom.equals("metric")) {
                tempSuffix = " C";
            }

            // non-databound way
            // currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + tempSuffix);

            // databound way
            binding.getCurTemp().temp.set(String.format("%.2f", main.getDouble("temp")) + tempSuffix);

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.e("LoboWeather", "something not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
}
