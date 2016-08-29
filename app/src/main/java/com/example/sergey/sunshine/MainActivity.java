package com.example.sergey.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sergey.sunshine.data.WeatherContract;
import com.example.sergey.sunshine.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    public String mLocation;
    public Boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);

        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));

        if (findViewById(R.id.weather_detail_container) != null){
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

//            In two-pane mode, show the detail view in this activity by
//            adding or replacing the detail fragment using a
//            fragment transaction.
            if (savedInstanceState == null) {
                    Uri dateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                            mLocation, System.currentTimeMillis());
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment().newInstance(dateUri), DETAILFRAGMENT_TAG)
                        .commit();
                    forecastFragment.mPosition = 0;
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        forecastFragment.SetUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        if ( null != ff ) {
            ff.onLocationChanged();
        }
        DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if ( null != df ) {
            df.onLocationChanged(location);
        }
        mLocation = location;
    }
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Uri dateUri) {

        if (mTwoPane){
            DetailFragment detailFragment = DetailFragment.newInstance(dateUri);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.weather_detail_container, detailFragment, DETAILFRAGMENT_TAG);
//            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(dateUri);
            startActivity(intent);
        }
    }
}
