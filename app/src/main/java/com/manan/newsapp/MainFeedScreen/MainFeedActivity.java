package com.manan.newsapp.MainFeedScreen;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.manan.newsapp.AlarmReceiver;
import com.manan.newsapp.ConnectivityReceiver;
import com.manan.newsapp.InternetUtilApplication;
import com.manan.newsapp.Model.DataModelClasses.ArticleData;
import com.manan.newsapp.Model.DataModelClasses.Source;
import com.manan.newsapp.Model.LocalServices.AppDatabase;
import com.manan.newsapp.Model.LocalServices.ArticleDao;
import com.manan.newsapp.Model.LocalServices.ArticleDao_Impl;
import com.manan.newsapp.Model.LocalServices.DatabaseInitializer;
import com.manan.newsapp.Model.RemoteServices.ApiClient;
import com.manan.newsapp.Model.RemoteServices.ApiInterface;
import com.manan.newsapp.Model.RemoteServices.AppLocationService;
import com.manan.newsapp.Model.DataModelClasses.NewsResponseModel;
import com.manan.newsapp.Model.RemoteServices.LocationAddress;
import com.manan.newsapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFeedActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;
    String COUNTRY_CODE;
    String API_KEY;
    String queryParam;
    AppLocationService appLocationService;
    ApiInterface apiInterface;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ArticlesTileAdapter mAdapter;
    private AppDatabase appDatabase;
    private SearchView searchView;
    List<ArticleData> mArticlesList = new ArrayList<>();
    List<ArticleData> offlineData = new ArrayList<>();
    private ProgressBar progressBar;

    private int mYear, mMonth, mDay;
    private FloatingActionButton dateSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main_feed );
        searchView = findViewById( R.id.search_bar );
        progressBar = findViewById( R.id.progress_bar );
        dateSearch = findViewById( R.id.filter_date );
        API_KEY = getResources().getString( R.string.api_auth_key );
        swipeRefreshLayout = findViewById( R.id.swiperefresh );
        recyclerView = (RecyclerView) findViewById( R.id.recycler_view );
        showPermissionDialog();
//Search Query Listener
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                progressBar.setVisibility( View.VISIBLE );
                queryParam = query;
                Toast.makeText( MainFeedActivity.this, "Showing Searched Results", Toast.LENGTH_SHORT ).show();
                getNewsFeed( true );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                queryParam = "";
                return false;
            }
        } );
//Handling Notification
        handleNotification();

        progressBar.setVisibility( View.VISIBLE );

//Location Service
        appLocationService = new AppLocationService(
                MainFeedActivity.this );

//API
        apiInterface = ApiClient.getClient().create( ApiInterface.class );

        checkConnection();
        Location gpsLocation = appLocationService
                .getLocation( LocationManager.GPS_PROVIDER );


        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
            String result = "Latitude: " + gpsLocation.getLatitude() +
                    " Longitude: " + gpsLocation.getLongitude();
            Log.d( "Check1", result + "" );
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation( latitude, longitude,
                    getApplicationContext(), new GeocoderHandler() );
        } else {
            showSettingsAlert();
        }

//Swipe Refresh
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {
                    Toast.makeText( MainFeedActivity.this, "Showing Online Data", Toast.LENGTH_SHORT ).show();
                    getNewsFeed( false );
                } else {
                    showSnack( isConnected );
                    swipeRefreshLayout.setRefreshing( false );
                    Toast.makeText( MainFeedActivity.this, "Showing Offline Data", Toast.LENGTH_SHORT ).show();
                    showOfflineData();

                }

            }
        } );

        dateSearch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        } );

    }
//Function for offline data


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView( name, context, attrs );
    }

    void showOfflineData() {
        ArticleDao_Impl articleDao = new ArticleDao_Impl( AppDatabase.getAppDatabase( getApplicationContext() ) );
        mArticlesList.clear();
        offlineData = articleDao.getAll();
        for (int i = 0; i < offlineData.size(); i++)
            offlineData.get( i ).setSource( new Source( "", "Not Available" ) );

        mAdapter = new ArticlesTileAdapter( offlineData, getApplicationContext() );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );

        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

    }

//Function for remote data

    void getNewsFeed(final boolean isSearch) {
        Call<NewsResponseModel> call = apiInterface.getHeadlines( COUNTRY_CODE, API_KEY, queryParam, null, 32 );
        call.enqueue( new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {

                Log.d( "Response", response.raw() + "" );
                mArticlesList = response.body().getArticles();
                offlineData.clear();
                mAdapter = new ArticlesTileAdapter( mArticlesList, getApplicationContext() );
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
                recyclerView.setLayoutManager( mLayoutManager );
                recyclerView.setItemAnimator( new DefaultItemAnimator() );
                recyclerView.setAdapter( mAdapter );
                swipeRefreshLayout.setRefreshing( false );
                progressBar.setVisibility( View.GONE );


                if (!isSearch)
                    DatabaseInitializer.populateAsync( AppDatabase.getAppDatabase( MainFeedActivity.this ), mArticlesList );
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {
                call.cancel();
                swipeRefreshLayout.setRefreshing( false );
                Log.d( "Response", t.getLocalizedMessage() + "" );
                progressBar.setVisibility( View.GONE );

            }

        } );

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainFeedActivity.this );
        alertDialog.setTitle( "SETTINGS" );
        alertDialog.setMessage( "Enable Location Provider! Go to settings menu?" );
        alertDialog.setPositiveButton( "Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                        MainFeedActivity.this.startActivity( intent );
                    }
                } );
        alertDialog.setNegativeButton( "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                } );
        alertDialog.show();
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString( "address" );
                    break;
                default:
                    locationAddress = null;
            }
            Log.d( "Country", locationAddress + "" );
            COUNTRY_CODE = locationAddress + "";

            progressBar.setVisibility( View.VISIBLE );
            getNewsFeed( false );

//
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        InternetUtilApplication.getInstance().setConnectivityListener( this );


    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make( findViewById( R.id.fab ), message, Snackbar.LENGTH_LONG );

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById( android.support.design.R.id.snackbar_text );
        textView.setTextColor( color );
        snackbar.show();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();

        progressBar.setVisibility( View.GONE );
        showSnack( isConnected );
        if (!isConnected) {
            Toast.makeText( MainFeedActivity.this, "Showing offline data", Toast.LENGTH_SHORT ).show();
            showOfflineData();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack( isConnected );
    }

    private void handleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE );

        Intent notificationIntent = new Intent( this, AlarmReceiver.class );
        ArticleDao_Impl articleDao = new ArticleDao_Impl( AppDatabase.getAppDatabase( getApplicationContext() ) );

        notificationIntent.putExtra( "title", articleDao.getAll().get( 5 ).getTitle() );
        notificationIntent.putExtra( "content", articleDao.getAll().get( 5 ).getContent() );
        PendingIntent broadcast = PendingIntent.getBroadcast( this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.SECOND, 60 * 60 * 4 );
        alarmManager.setExact( AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast );


    }

    void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get( Calendar.YEAR );
        mMonth = c.get( Calendar.MONTH );
        mDay = c.get( Calendar.DAY_OF_MONTH );

        DatePickerDialog datePickerDialog = new DatePickerDialog( this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (offlineData.size() == 0) {
                            dateChecker( mArticlesList, year, monthOfYear, dayOfMonth );
                        } else {
                            dateChecker( offlineData, year, monthOfYear, dayOfMonth );

                        }
                    }
                }, mYear, mMonth, mDay );
        datePickerDialog.show();
    }

    void dateChecker(List<ArticleData> data, int year,
                     int monthOfYear, int dayOfMonth) {
        DateFormat inputFormat = new SimpleDateFormat( "yyyy/M/dd" );
        DateFormat outputFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        String stDate = String.valueOf( year ) + "/" + String.valueOf( monthOfYear + 1 ) + "/" + String.valueOf( dayOfMonth );
        Date date = null;
        try {
            date = inputFormat.parse( stDate );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format( date );
        Log.d( "Date", data.size() + "" );
        int i = 0;

        while (i < data.size()) { //Log.d( "Date", data.get( i ).getPublishedAt().substring( 0,10 )+""+i );
            String check = data.get( i ).getPublishedAt().substring( 0, 10 );
            if (!check.equals( outputDateStr )) {
                data.remove( i );
            }

            i++;
        }
        Toast.makeText( this, "Changes has been Updated", Toast.LENGTH_SHORT ).show();
        Log.d( "Date", data.size() + "" );
        mAdapter = new ArticlesTileAdapter( data, getApplicationContext() );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

    }

    public void showPermissionDialog() {
        if (!AppLocationService.checkPermission( MainFeedActivity.this )) {
            ActivityCompat.requestPermissions(
                    MainFeedActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

    }
}
