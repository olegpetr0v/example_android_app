package com.example.olp.testapp6;

import
android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {

    private LocationManager locationManager;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor orientation;
    private Sensor magnetic;

    private float maxrange;

    private TextView currentX, currentY, currentZ, maxAccValue;
    private TextView currentA, currentP, currentR;
    private TextView currentMX, currentMY, currentMZ;
    private TextView tvStatusGPS, tvLocationGPS, tvEnabledGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        maxrange = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getMaximumRange();
        maxAccValue.setText(Float.toString(maxrange));

        orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_FASTEST);

        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_FASTEST);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        maxAccValue = (TextView) findViewById(R.id.maxAccValue);

        currentA = (TextView) findViewById(R.id.currentA);
        currentP = (TextView) findViewById(R.id.currentP);
        currentR = (TextView) findViewById(R.id.currentR);

        currentMX = (TextView) findViewById(R.id.currentMX);
        currentMY = (TextView) findViewById(R.id.currentMY);
        currentMZ = (TextView) findViewById(R.id.currentMZ);

        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_FASTEST);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        checkEnabled();
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            tvStatusGPS.setText("Status: " + String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String s) {
            checkEnabled();
        }

        @Override
        public void onProviderDisabled(String s) {
            checkEnabled();
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        tvLocationGPS.setText(formatLocation(location));
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] mAcc, mOr, mMag;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAcc = event.values;
            currentX.setText(Float.toString(mAcc[0]));
            currentY.setText(Float.toString(mAcc[1]));
            currentZ.setText(Float.toString(mAcc[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            mOr = event.values;
            currentA.setText(Float.toString(mOr[0]));
            currentP.setText(Float.toString(mOr[1]));
            currentR.setText(Float.toString(mOr[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            mMag = event.values;
            currentMX.setText(Float.toString(mMag[0]));
            //currentMX.setText(Float.toString((float) Math.sqrt((float) Math.pow(mMag[0], 2) + (float) Math.pow(mMag[1], 2) + (float) Math.pow(mMag[2], 2))));
            currentMY.setText(Float.toString(mMag[1]));
            currentMZ.setText(Float.toString(mMag[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
