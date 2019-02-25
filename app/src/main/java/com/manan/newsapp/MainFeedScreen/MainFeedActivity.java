package com.manan.newsapp.MainFeedScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.manan.newsapp.Model.RemoteServices.AppLocationService;
import com.manan.newsapp.Model.RemoteServices.LocationAddress;
import com.manan.newsapp.R;

public class MainFeedActivity extends AppCompatActivity {

    AppLocationService appLocationService;
    String COUNTRY_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main_feed );

        appLocationService = new AppLocationService(
                MainFeedActivity.this );


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
//
        }
    }
}
