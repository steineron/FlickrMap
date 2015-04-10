package com.flickrmap.flickrmap.controller.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.flickrmap.flickrmap.R;
import com.flickrmap.flickrmap.controller.AppPhotoBundle;
import com.flickrmap.flickrmap.controller.fragments.PhotoGalleryFragment;
import com.flickrmap.flickrmap.model.GetPhotosService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;

import java.util.ArrayList;
import java.util.HashMap;

import auto.parcel.AutoParcel;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener, GetPhotosService.OnPhotosResultListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MapFragment mMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver mPhotosResultsReceiver;
    private HashMap<String, AppPhotoBundle> mAppPhotosMap;
    private PhotoGalleryFragment mPhotoGalleryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mMapFragment = new MapFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mMapFragment)
                    .commit();

            mMapFragment.getMapAsync(this);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        mPhotosResultsReceiver = GetPhotosService.registerOnPhotosResultListener(this, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        try {
            unregisterReceiver(mPhotosResultsReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Location currentLocation =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        startService(GetPhotosService
                .createGetPhotosByLocationServiceIntent(
                        this,
                        currentLocation,
                        100));
        return false;
    }

    private void populatePhotosOnMap(ArrayList<Photo> photos) {

        // remove all markers

        // re-populate makers

        mAppPhotosMap = new HashMap<String, AppPhotoBundle>();
        int n = 0;
        for (Photo photo : photos) {
            try {
                GeoData geoData = photo.getGeoData();
                LatLng position = new LatLng(geoData.getLatitude(), geoData.getLongitude());
                MarkerOptions makerOptions = new MarkerOptions()
                        .position(position)
                        .title(String.valueOf(++n))
                        .snippet(photo.getDescription());
                Marker marker = mMapFragment.getMap()
                        .addMarker(makerOptions);
                String markerId = marker.getId();
                AppPhotoBundle appPhotoBundle = createAppPhotoBundle(markerId, photo);
                Log.v(TAG, "adding marker for: " + appPhotoBundle.toString() + " at: " +
                        position.toString());

                mAppPhotosMap.put(markerId, appPhotoBundle);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private AppPhotoBundle createAppPhotoBundle(String markerId, Photo photo) {
        return AppPhotoBundleImpl.create(markerId,
                photo.getLargeUrl(),
                photo.getThumbnailUrl(),
                photo.getTitle(),
                photo.getDescription());
    }

    @Override
    public void onPhotosResult(Context context, ArrayList<Photo> photos) {
        populatePhotosOnMap(photos);
        populateThumbsGallery();
    }

    private void populateThumbsGallery() {
        ArrayList<AppPhotoBundle> appPhotoBundles =
                new ArrayList<AppPhotoBundle>(mAppPhotosMap.values());
        if (mPhotoGalleryFragment == null) {
            mPhotoGalleryFragment = PhotoGalleryFragment.newInstance(appPhotoBundles);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPhotoGalleryFragment)
                    .commit();
        }
        else {
            mPhotoGalleryFragment.setPhotosList(appPhotoBundles);
        }
    }

    @Override
    public void onFault(Context context) {

    }

    @AutoParcel
    static abstract class AppPhotoBundleImpl implements AppPhotoBundle {
        static AppPhotoBundleImpl create(
                @Nullable String markerId,
                @Nullable String largeUrl,
                @Nullable String thumbnailUrl,
                @Nullable String title,
                @Nullable String description) {
            return new AutoParcel_MainActivity_AppPhotoBundleImpl(markerId, largeUrl, thumbnailUrl, title, description);
        }
    }
}
