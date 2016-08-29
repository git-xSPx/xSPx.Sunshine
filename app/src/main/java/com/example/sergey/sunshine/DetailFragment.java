package com.example.sergey.sunshine;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergey.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private String mForecastStr;
    private static DetailViewHolder mDetailViewHolder;

    private static final int FORECAST_DETAIL_LOADER_ID = 0;
    private ShareActionProvider mShareActionProvider = null;

    public static final String DETAIL_URI = "URI";

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;
    private Uri mDateUri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public static DetailFragment newInstance(Uri dateUri) {
        DetailFragment detailFragment = new DetailFragment();

        detailFragment.setArguments(getUriBundle(dateUri));

        return detailFragment;
    }

    public static Bundle getUriBundle(Uri dateUri) {
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, dateUri);
        return args;
    }

    public Uri getUri() {
        Bundle args = getArguments();

        if (args == null) {
            return null;
        }

        return args.getParcelable(DETAIL_URI);
    }

    public void updateUri(Uri dateUri){
        mDateUri = dateUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDetailViewHolder = new DetailViewHolder(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share_forecast);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mForecastStr != null) {
            setShareIntent(createShareForecastIntent());
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareForecastIntent() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

        return intent;
    }

    private void setShareIntent(Intent shareIntent) {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
        else {
            Log.d(LOG_TAG, "Share Action Provider is null!");
        }
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null == mDateUri) {
            mDateUri = getUri();
        }

        if (null != mDateUri) {
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
            return new CursorLoader(getActivity(), mDateUri, FORECAST_COLUMNS, null, null, sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) { return; }

        Context context = getActivity();

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        String dayString  = Utility.getDayName(context, data.getLong(COL_WEATHER_DATE));
        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String weatherDescription = data.getString(COL_WEATHER_DESC);

        String high = Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MAX_TEMP));
        String low  = Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MIN_TEMP));
        String wind = Utility.getFormattedWind(context,  data.getFloat(COL_WEATHER_WIND_SPEED),
                                                         data.getFloat(COL_WEATHER_DEGREES));
        float windDirection = data.getFloat(COL_WEATHER_DEGREES);
        String humidity = String.format(context.getString(R.string.format_humidity), data.getFloat(COL_WEATHER_HUMIDITY));
        String pressure = String.format(context.getString(R.string.format_pressure), data.getFloat(COL_WEATHER_PRESSURE));

        mForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        if (mDetailViewHolder != null) {
            mDetailViewHolder.dayView.setText(dayString);
            mDetailViewHolder.dateView.setText(dateString);
            mDetailViewHolder.descriptionView.setText(weatherDescription);
            mDetailViewHolder.highTempView.setText(high);
            mDetailViewHolder.lowTempView.setText(low);
            mDetailViewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            mDetailViewHolder.iconView.setContentDescription(weatherDescription);
            mDetailViewHolder.humidityView.setText(humidity);
            mDetailViewHolder.windTextView.setText(R.string.wind);
            mDetailViewHolder.windView.setText(wind);
            mDetailViewHolder.windView.setContentDescription(wind);
            mDetailViewHolder.windView.setWindDirection(windDirection);
            mDetailViewHolder.pressureView.setText(pressure);
        }
        if (mShareActionProvider != null) {
            setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onLocationChanged( String newLocation ) {
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, System.currentTimeMillis());
            updateUri(updatedUri);
            getLoaderManager().restartLoader(FORECAST_DETAIL_LOADER_ID, null, this);
    }

    public static class DetailViewHolder {
        public final ImageView iconView;
        public final TextView dayView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView humidityView;
        public final TextView windTextView;
        public final WindView windView;
        public final TextView pressureView;

        public DetailViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dayView = (TextView) view.findViewById(R.id.day_textview);
            dateView = (TextView) view.findViewById(R.id.date_textview);
            descriptionView = (TextView) view.findViewById(R.id.forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.high_textview);
            lowTempView  = (TextView) view.findViewById(R.id.low_textview);
            humidityView = (TextView) view.findViewById(R.id.humidity_textview);
            windTextView = (TextView) view.findViewById(R.id.wind_textview);
            windView     = (WindView) view.findViewById(R.id.wind_view);
            pressureView = (TextView) view.findViewById(R.id.pressure_textview);
        }
    }
}
