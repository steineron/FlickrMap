package com.flickrmap.flickrmap.controller.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.flickrmap.flickrmap.R;
import com.flickrmap.flickrmap.controller.AppPhotoDetails;
import com.flickrmap.flickrmap.controller.AppPhotosIntents;
import com.flickrmap.flickrmap.controller.fragments.PhotoGalleryFragment;
import com.flickrmap.flickrmap.controller.fragments.PhotosMapFragment;
import com.flickrmap.flickrmap.model.GetPhotosService;
import com.flickrmap.flickrmap.model.VolleyWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import auto.parcel.AutoParcel;


public class MainActivity extends ActionBarActivity implements PhotosMapFragment.OnMapPhotosChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private MapFragment mMapFragment;

    private PhotoGalleryFragment mPhotoGalleryFragment;

    private BroadcastReceiver mMapPhotosChangedReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapPhotosChangedReceiver = PhotosMapFragment.registerOnMapPhotosChangeListener(this, this);
        if (savedInstanceState == null) {
            // add the map fragment, load the map.
            mMapFragment = new PhotosMapFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mMapFragment)
                    .commit();

        }
    }

    @Override
    protected void onDestroy() {

        try {
            unregisterReceiver(mMapPhotosChangedReceiver);
        }
        catch (Exception e) {

        }
        super.onDestroy();
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
        if (id == R.id.action_clear) {
            removeThumbsGalleryFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapPhotosAdded(final Context context, final Collection<AppPhotoDetails> photos) {

        if (mPhotoGalleryFragment == null) {
            mPhotoGalleryFragment = PhotoGalleryFragment.newInstance(photos);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPhotoGalleryFragment)
                    .commit();
        }
    }

    @Override
    public void onMapPhotosRemoved(final Context context, final Collection<AppPhotoDetails> photos) {

    }

    @Override
    public void onMapPhotosCleared(final Context context) {

        removeThumbsGalleryFragment();
    }


    /*private*/


    private void removeThumbsGalleryFragment(){
        if (mPhotoGalleryFragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(mPhotoGalleryFragment)
                    .commit();
            mPhotoGalleryFragment=null;
        }
    }

}
