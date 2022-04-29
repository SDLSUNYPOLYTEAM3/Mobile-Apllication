package com.example.sdl_mobileinterface;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class ETA_calc extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private IMapController mc = null;
    private MyLocationNewOverlay mLocationOverlay = null;
    private LocationManager lm = null;
    private LocationListener locListener = null;

    private Boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first


        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_eta_calc);

        map = (MapView) findViewById(R.id.MapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setClickable(true);
//        map.setSatellite(true);
//        map.setTraffic(true);


        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                //WRITE_EXTERNAL_STORAGE is required in order to show the map,
                WRITE_EXTERNAL_STORAGE
        });

        //mLocationOverlay.enableMyLocation();
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        this.mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(this.mLocationOverlay);

        //center map to user
        mc = map.getController();
        mc.animateTo(mLocationOverlay.getMyLocation());
        //Log.d("UserActivity", mLocationOverlay.getMyLocation().toString());
        mc.zoomTo(18);
        //mc.setCenter(startPoint);

        //ETA calc
        //Location gpsLocation = getLocation(LocationManager.GPS_PROVIDER);

//        if (gpsLocation != null) {
//            double latitude = gpsLocation.getLatitude();
//            double longitude = gpsLocation.getLongitude();
//            Toast.makeText(
//                    getApplicationContext(),
//                    "Mobile Location (GPS): \nLatitude: " + latitude
//                            + "\nLongitude: " + longitude,
//                    Toast.LENGTH_LONG).show();
//        }


//        Location loc = null;
//        String longitude = "Longitude: " + loc.getLongitude();
//        Log.d("ETA", longitude);
//        String latitude = "Latitude: " + loc.getLatitude();
//        Log.d("ETA", latitude);



        //set geopoints
        //GeoPoint lot1 = new GeoPoint(43.123956, -75.226755);
        //GeoPoint lotF = new GeoPoint(43.140963, -75.228459);

//        anotherOverlayItemArray = new ArrayList<OverlayItem>();
//        anotherOverlayItemArray.add(new OverlayItem(
//                "0, 0", "0, 0", new GeoPoint(0, 0)));

    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("ETA", "latitude: " + (String.valueOf(location.getLatitude())));
            Log.d("ETA", "longitude: " + (String.valueOf(location.getLongitude())));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);
        }
    };

//    // Now first make a criteria with your requirements
//    // this is done to save the battery life of the device
//    // there are various other other criteria you can search for..
//    Criteria criteria = new Criteria();
//    criteria.setAccuracy(Criteria.ACCURACY_FINE);
//    criteria.setPowerRequirement(Criteria.POWER_LOW);
//    criteria.setAltitudeRequired(false);
//    criteria.setBearingRequired(false);
//    criteria.setSpeedRequired(false);
//    criteria.setCostAllowed(true);
//    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
//    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
//
//
//    // Now create a location manager
//    final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//
//    // This is the Best And IMPORTANT part
//    final Looper looper = null;
//
//    // Now whenever the button is clicked fetch the location one time
//    locationManager.requestSingleUpdate(criteria, locationListener, looper);


}






//    //lm = (LocationManager);
//    getSystemService(Context.LOCATION_SERVICE);
//        flag = displayGpsStatus();
//                if (flag) {
//                locListener = new MyLocationListener();
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//                }
//                //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locListener);
//                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locListener);
//                }else {
//                alertbox("Gps Status!!", "Your GPS is: OFF");
//                }

//        Location loc = null;
//        String longitude = "Longitude: " +loc.getLongitude();
//        Log.d("ETA", longitude);
//        String latitude = "Latitude: " +loc.getLatitude();
//        Log.d("ETA", latitude);


//    //Check GPS is enable or disable
//    private Boolean displayGpsStatus() {
//        ContentResolver contentResolver = getBaseContext()
//                .getContentResolver();
//        boolean gpsStatus = Settings.Secure
//                .isLocationProviderEnabled(contentResolver,
//                        LocationManager.GPS_PROVIDER);
//        if (gpsStatus) {
//            return true;
//
//        } else {
//            return false;
//        }
//    }
//
//    //Method to create an AlertBox
//    protected void alertbox(String title, String mymessage) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Your Device's GPS is Disable")
//                .setCancelable(false)
//                .setTitle("** Gps Status **")
//                .setPositiveButton("Gps On",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // finish the current activity
//                                // AlertBoxAdvance.this.finish();
//                                Intent myIntent = new Intent(
//                                        Settings.ACTION_SECURITY_SETTINGS);
//                                startActivity(myIntent);
//                                dialog.cancel();
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // cancel the dialog box
//                                dialog.cancel();
//                            }
//                        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
////Listener class to get coordinates
//private class MyLocationListener implements LocationListener {
//    @Override
//    public void onLocationChanged(Location loc) {
//
//        String longitude = "Longitude: " +loc.getLongitude();
//        Log.d("ETA", longitude);
//        String latitude = "Latitude: " +loc.getLatitude();
//        Log.d("ETA", latitude);
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onStatusChanged(String provider,
//                                int status, Bundle extras) {
//        // TODO Auto-generated method stub
//    }
//}





//    void AccessPermission(){
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1001);
//        }
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1002);
//        }
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//                checkSelfPermission(Manifest.permission.INTERNET)
//                        != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.INTERNET},1003);
//        }
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
//                        != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1004);
//        }
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//                checkSelfPermission(WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},1005);
//        }
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1006);
//        }
//
//
//
//    }
