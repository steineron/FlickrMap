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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.flickrmap.flickrmap.R;
import com.flickrmap.flickrmap.controller.AppPhotoDetails;
import com.flickrmap.flickrmap.controller.fragments.PhotoGalleryFragment;
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
import java.util.HashMap;

import auto.parcel.AutoParcel;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener, GetPhotosService.OnPhotosResultListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int INITIAL_ZOOM = 12; // this should provide a ~50KM radius

    private MapFragment mMapFragment;

    private GoogleApiClient mGoogleApiClient;

    private BroadcastReceiver mPhotosResultsReceiver;

    private HashMap<String, AppPhotoDetails> mAppPhotosMap;

    private PhotoGalleryFragment mPhotoGalleryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            // add the map fragment, load the map.
            // connect the api-client
            // once the client connects  - start loading the photos for the current location.
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

        // register a listener to handle retrieval of photos
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
        googleMap.setInfoWindowAdapter(new PhotoDetailsWindowAdapter());

    }

    private Location getCurrentLocation() {

        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // hte google api client is connected - get current location and zoom to it

        GoogleMap googleMap = mMapFragment == null ?
                              null :
                              mMapFragment.getMap();
        if (googleMap != null) {
            // Move the camera instantly to hamburg with a zoom of 15.
            Location currentLocation = getCurrentLocation();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), INITIAL_ZOOM));

            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM), 2000, null);

        }
        // now retrieve phot's details for this location
        retrievePhotosForLocation(getCurrentLocation());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        retrievePhotosForLocation(getCurrentLocation());
        return false;
    }

    private void retrievePhotosForLocation(Location location) {

        startService(GetPhotosService
                .createGetPhotosByLocationServiceIntent(
                        this,
                        location != null ?
                        location :
                        getCurrentLocation(),
                        100));
    }

    private void populatePhotosOnMap(ArrayList<Photo> photos) {

        // remove all markers

        // re-populate makers

        mAppPhotosMap = new HashMap<String, AppPhotoDetails>(); // will clear older references
        int n = 0;
        for (Photo photo : photos) {
            try {
                GeoData geoData = photo.getGeoData();
                LatLng position = new LatLng(geoData.getLatitude(), geoData.getLongitude());
                MarkerOptions makerOptions = new MarkerOptions()
                        .position(position);
                Marker marker = mMapFragment.getMap()
                        .addMarker(makerOptions);
                String markerId = marker.getId();
                AppPhotoDetails appPhotoDetails = createAppPhotoDetails(markerId, photo);
                Log.v(TAG, "adding marker for: " + appPhotoDetails.toString() + " at: " +
                        position.toString());

                // keep a mapping from marker to photo-bundle so events identified by the marker's id can be related to a photo's details
                mAppPhotosMap.put(markerId, appPhotoDetails);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private AppPhotoDetails createAppPhotoDetails(String markerId, Photo photo) {

        return AppPhotoDetailsImpl.create(markerId,
                photo.getSmallSquareUrl(), // should be 75x75
                photo.getMediumUrl(),
                photo.getTitle(),
                photo.getDescription());
    }

    @Override
    public void onPhotosResult(Context context, ArrayList<Photo> photos) {

        populatePhotosOnMap(photos);
        populateThumbsGallery();
    }

    private void populateThumbsGallery() {

        ArrayList<AppPhotoDetails> listAppPhotoDetails =
                new ArrayList<AppPhotoDetails>(mAppPhotosMap.values());
        if (mPhotoGalleryFragment == null) {
            mPhotoGalleryFragment = PhotoGalleryFragment.newInstance(listAppPhotoDetails);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPhotoGalleryFragment)
                    .commit();
        }
        else {
            mPhotoGalleryFragment.setPhotosList(listAppPhotoDetails);
        }
    }

    @Override
    public void onFault(Context context) {

    }

    /**
     * {@link AppPhotoDetailsImpl} - an internal implementation for the {@link AppPhotoDetails} interface
     * the current activity creates and manages instances of this class.
     * each instance is coupled to a maker on the map (using {@link Marker:getId})
     * the mapping takes place via {@value MainActivity:mAppPhotosMap}     *
     */
    @AutoParcel
    static abstract class AppPhotoDetailsImpl implements AppPhotoDetails {

        private Bitmap mLargeBitmap;

        public Bitmap getLargeBitmap() {

            return mLargeBitmap;
        }

        public void setLargeBitmap(@Nullable Bitmap largeBitmap) {

            mLargeBitmap = largeBitmap;
        }

        static AppPhotoDetailsImpl create(
                @Nullable String markerId,
                @Nullable String thumbnailUrl,
                @Nullable String largeUrl,
                @Nullable String title,
                @Nullable String description) {

            return new AutoParcel_MainActivity_AppPhotoDetailsImpl(markerId, thumbnailUrl, largeUrl, title, description);
        }
    }

    /**
     * {@link PhotoDetailsWindowAdapter} runs the adapter to match a marker on the map with a custom-info view
     */
    private class PhotoDetailsWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {

            return null; // default buble-style
        }

        @Override
        public View getInfoContents(Marker marker) {

            final AppPhotoDetailsImpl photo =
                    (AppPhotoDetailsImpl) mAppPhotosMap.get(marker.getId());
            View view = null;

            if (photo != null && photo.getLargeBitmap() != null) {
                view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.marker_info_window, null, false);
                final ImageView imageView = (ImageView) view.findViewById(R.id.info_window_image);
                imageView.setImageBitmap(photo.getLargeBitmap());
                final TextView textView = (TextView) view.findViewById(R.id.info_window_text);
                final String title = photo.getTitle();
                if (title != null) {
                    textView.setText(title);
                }
                else {
                    textView.setVisibility(View.GONE);
                }
            }
            else {

                getImageForViewAndRefresh(marker, photo);
            }
            return view;
        }

        private ImageRequest getImageForViewAndRefresh(final Marker marker,
                                                       final AppPhotoDetailsImpl photo) {

            ImageRequest imageRequest = new ImageRequest(photo.getLargeSizeUrl(),
                    new Response.Listener<Bitmap>() {

                        @Override
                        public void onResponse(Bitmap bitmap) {

                            photo.setLargeBitmap(bitmap);
                            marker.showInfoWindow();
                        }

                    }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                    new Response.ErrorListener() {

                        public void onErrorResponse(VolleyError error) {

                            //TODO: handle the error properly - display error image
                        }
                    });
            // launch the request
            VolleyWrapper.getInstance(MainActivity.this)
                    .addToRequestQueue(imageRequest);
            return imageRequest;

        }
    }
}
