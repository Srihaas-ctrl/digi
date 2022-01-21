package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_temp;
    Switch sw1, sw2;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    LocationBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;


    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_labellat);
        tv_lon = findViewById(R.id.tv_labellon);
        tv_temp = findViewById(R.id.tv_labeltemp);
        sw1 = findViewById(R.id.sw_GPS);
        sw2 = findViewById(R.id.sw_TEMP);

        receiver = new LocationBroadcastReceiver();
         mIntentFilter = new IntentFilter("action_name");

        if (Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //Req Location Permission
                sw1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(sw1.isChecked()){
                            Intent intent = new Intent(MainActivity.this, LocationService.class);
                            startService(intent);
                            updateGPS();

                        }
                        else{
                            tv_lon.setText("Lon:");
                            tv_lat.setText("Lat:");
                        }
                    }
                });
            }
        } else {
            //Start the Location Service
            sw1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sw1.isChecked()){
                        Intent intent = new Intent(MainActivity.this, LocationService.class);
                        startService(intent);

                    }
                    else{
                        tv_lon.setText("Lon:");
                        tv_lat.setText("Lat:");
                    }
                }
            });
        }


        sw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sw2.isChecked()){
                    Random random = new Random();
                    tv_temp.setText(String.valueOf(random.nextInt(31-20)+20));
                }
                else{
                    tv_temp.setText("Temp");
                }
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, mIntentFilter);
    }
    @Override
    protected void onPause() {
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onPause();
    }

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));

    }




    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ACT_LOC")){
                double lat = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                tv_lat.setText(String.valueOf(lat));
                tv_lon.setText(String.valueOf(longitude));
                Toast.makeText(MainActivity.this, "latitude is " +lat+ "longitude is " + longitude, Toast.LENGTH_SHORT).show();
            }
        }
    }


}




