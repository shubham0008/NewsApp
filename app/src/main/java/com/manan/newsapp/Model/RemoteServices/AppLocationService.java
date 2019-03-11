package com.manan.newsapp.Model.RemoteServices;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.manan.newsapp.MainFeedScreen.MainFeedActivity;

public class AppLocationService extends Service implements LocationListener {

    protected LocationManager locationManager;
    Location location;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public AppLocationService(Context context) {
        checkPermission( context );
        locationManager = (LocationManager) context
                .getSystemService( LOCATION_SERVICE );
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(String provider) {
        if (locationManager.isProviderEnabled( provider )) {

                locationManager.requestLocationUpdates( provider,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this );
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider);
                    return location;

            }
            else {
              MainFeedActivity activity = new MainFeedActivity();
              activity.showPermissionDialog();
                //  public static void requestPermissions (MainFeedActivity activity, String[] permissions, int requestCode)

            }


        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
