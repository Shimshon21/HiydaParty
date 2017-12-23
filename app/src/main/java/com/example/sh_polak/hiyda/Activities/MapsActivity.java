package com.example.sh_polak.hiyda.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.PremissionManger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location location;//last known location
    private Marker myMarker;
    private LocationManager locationManager;//to work with location service



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);//u need to us location service by the system
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            //check for available location provider
           // if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) Log.i("My locations","********GPS enabled*******");
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))Log.i("My locations","********Network: Wifi & 3G,4G... enabled*******");
            //OR get best provider by Criteria
            Criteria c=new Criteria();//indicates criteria for location provider selection or in my words with this u could to config the all option of the gps like power and which provider to use
            c.setAccuracy(c.POWER_MEDIUM);//low battery consumption
            String bestProvider=locationManager.getBestProvider(c, true);//best provider for given criteria
            Log.i("NullPointerCheck",bestProvider+" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            locationManager.getLastKnownLocation(bestProvider);//get last known location from given provider
            //register location listener listener with GPS
           this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1,new MyLocListener());//requaestLocation get provider,every which time look for location update ,distance which u passed, which merhod to invoke/
        }catch (SecurityException e){
            Log.e("My maps","******Almog*********Location service - disabled - permission rejected");
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user_fragment will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user_fragment has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(32, 34.9)));//Map center - animated
        this.addMapListeners();//
        this.mMap.getMaxZoomLevel();
        myMarker = this.addMarker("Hackeru HQ", new LatLng(32.084422, 34.800719));//Pin marker - HackerU HQ
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.084422, 34.800719),16.0f));
    }
    private Marker addMarker(String title, LatLng latLng){
        return this.mMap.addMarker(new MarkerOptions().title(title).position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
    }

    private void addMapListeners(){
        this.mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(final LatLng latLng) {//geo location - where was long click
                Log.i("touched at ", latLng.toString());
                final EditText input = new EditText(MapsActivity.this);
                new AlertDialog.Builder(MapsActivity.this).setTitle("Add marker").setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {//Adding marker at location
                                addMarker(input.getText().toString(),latLng);
                            }
                        }).show();
            }
        });
        this.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {//What to do - when pin Marker clicked
                Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    private class MyLocListener implements LocationListener {// location is not changing somehow.
        @Override//when location changed
        public void onLocationChanged(Location location) {

            Log.i("Location Check "," my location"+location.getLatitude() + location.getLongitude() +"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if(location != null){
                Log.i("  My - location","*************Moved to "+location.getLatitude()+","+location.getLongitude()+" **************");
                //store new current location
                MapsActivity.this.location=location;
                //LatLng langLat = new LatLng(location.getLongitude(),);
                //change location for existing marker
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
            myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),11f));
                mMap.getMaxZoomLevel();
              myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder));
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    }


}
