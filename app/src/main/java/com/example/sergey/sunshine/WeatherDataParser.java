package com.example.sergey.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sergey on 23.02.15.
 */
public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */

    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here

        JSONObject weatherJsonObj = new JSONObject(weatherJsonStr);
        JSONArray jsonArrayOfDays = weatherJsonObj.getJSONArray("list");

        if (jsonArrayOfDays.length() == 0) {
            return -1000;
        }

        JSONObject dayJsonObj = jsonArrayOfDays.getJSONObject(dayIndex);

        JSONObject tempJsonObj = dayJsonObj.getJSONObject("temp");

        return tempJsonObj.optDouble("max", -1000);

//        return -1;
    }
}
