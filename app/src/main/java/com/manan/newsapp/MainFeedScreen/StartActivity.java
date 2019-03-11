package com.manan.newsapp.MainFeedScreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.manan.newsapp.Model.RemoteServices.AppLocationService;
import com.manan.newsapp.R;

public class StartActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_start );

        if (ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            startActivity( new Intent( StartActivity.this, MainFeedActivity.class ) );

        } else {
            showPermissionDialog();
        }
    }

    public void showPermissionDialog() {
        if (!AppLocationService.checkPermission( this )) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                startActivity( new Intent( StartActivity.this, MainFeedActivity.class ) );

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;


            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
