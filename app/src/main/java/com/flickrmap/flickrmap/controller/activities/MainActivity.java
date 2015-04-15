package com.flickrmap.flickrmap.controller.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.flickrmap.flickrmap.R;
import com.flickrmap.flickrmap.controller.AppPhotoDetails;
import com.flickrmap.flickrmap.controller.ControllerIntents;
import com.flickrmap.flickrmap.controller.fragments.PhotoGalleryFragment;
import com.flickrmap.flickrmap.controller.fragments.PhotosMapFragment;
import com.google.android.gms.maps.MapFragment;

import java.util.Collection;

/**
 * the main activity is mainly responsible for loading the map fragment and handling of the thumbs fragment
 * it also listens to changes in the map fragment and manipulated the gallery fragment as needed
 */
public class MainActivity extends ActionBarActivity implements
                                                    PhotosMapFragment.OnMapPhotosChangeListener {

    private MapFragment mMapFragment;

    private PhotoGalleryFragment mPhotoGalleryFragment;

    private BroadcastReceiver mMapPhotosChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //register a listener to handle changes to photos (markers) on the map
        mMapPhotosChangedReceiver =
                PhotosMapFragment.registerOnMapPhotosChangeListener(this, this);


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
        } catch (Exception e) {

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
            sendBroadcast(ControllerIntents.createClearAllPhotosIntent(this));
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
    public void onMapPhotosRemoved(final Context context,
                                   final Collection<AppPhotoDetails> photos) {

    }

    @Override
    public void onMapPhotosCleared(final Context context) {

        removeThumbsGalleryFragment();
    }


    /*private*/


    private void removeThumbsGalleryFragment() {

        if (mPhotoGalleryFragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(mPhotoGalleryFragment)
                    .commit();
            mPhotoGalleryFragment = null;
        }
    }


}
