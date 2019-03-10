package com.manan.newsapp.MainFeedScreen;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.manan.newsapp.AlarmReceiver;
import com.manan.newsapp.ConnectivityReceiver;
import com.manan.newsapp.InternetUtilApplication;
import com.manan.newsapp.Model.DataModelClasses.ArticleData;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFeedActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    String COUNTRY_CODE;
    String API_KEY;
    AppLocationService appLocationService;
    ApiInterface apiInterface;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ArticlesTileAdapter mAdapter;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main_feed );

      //  handleNotification();

        checkConnection();
        appLocationService = new AppLocationService(
                MainFeedActivity.this );
        apiInterface = ApiClient.getClient().create( ApiInterface.class );
        API_KEY = getResources().getString( R.string.api_auth_key);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Location gpsLocation = appLocationService
                .getLocation( LocationManager.GPS_PROVIDER );


        if (gpsLocation != null ) {
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
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean isConnected = ConnectivityReceiver.isConnected();
                if(isConnected) {
                    getNewsFeed();
                }
                else
                {
                    showSnack(isConnected);
                    swipeRefreshLayout.setRefreshing(false);

                    ArticleDao_Impl articleDao = new ArticleDao_Impl(AppDatabase.getAppDatabase( getApplicationContext()) );
                    List<ArticleData> offlineData = articleDao.getAll();
                    Log.e( "DATA",offlineData.size()+"" );
                    mAdapter = new ArticlesTileAdapter(offlineData,getApplicationContext());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);


                }

            }
        } );

    }

    void getNewsFeed() {
        Call<NewsResponseModel> call = apiInterface.getHeadlines(COUNTRY_CODE,API_KEY,null,null,32);
        call.enqueue( new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {

                Log.d( "Response",response.raw()+"" );
                List<ArticleData> mArticlesList = response.body().getArticles();
                mAdapter = new ArticlesTileAdapter(mArticlesList,getApplicationContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                swipeRefreshLayout.setRefreshing(false);
               DatabaseInitializer.populateAsync(AppDatabase.getAppDatabase(MainFeedActivity.this),mArticlesList);
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {
                call.cancel();
                swipeRefreshLayout.setRefreshing(false);

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
            getNewsFeed();

//
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        InternetUtilApplication.getInstance().setConnectivityListener(this);


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
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack( isConnected );
    }

    private void handleNotification() {
        Intent alarmIntent = new Intent(MainFeedActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pendingIntent);
    }
}
