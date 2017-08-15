package com.lordlobo.loboweather.networking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lordlobo.loboweather.R;
import com.lordlobo.loboweather.utility.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherNetworking {

    // q=%s&units=%s - basic query
    // zip=[zip],[countryCode] for zip query
    public static JSONObject getJSON(Context context, String city, String units){
        try {
            URL url = new URL(String.format(Constants.WeatherAPI, city, units));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            String apiId = retrieveApiId(context);
            connection.addRequestProperty("x-api-key", apiId);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp;

            while ((tmp = reader.readLine()) != null )
                json.append(tmp).append("\n");

            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static String retrieveApiId(Context context) {
        // we're going to store the app id in prefs so we don't need to keep a key in source control
        Activity ac = (Activity)context;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ac.getBaseContext());

        String apiId = prefs.getString("apiId", "");

        if (apiId.equals("")) {
            // get the id from the strings.xml file
            apiId = context.getString(R.string.open_weather_maps_app_id);
            prefs.edit().putString("apiId", apiId).apply();

            // after first run you should be able to remove the api key from strings.xml
        }

        return apiId;
    }
}