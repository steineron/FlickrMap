package com.flickrmap.flickrmap.controller.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.flickrmap.flickrmap.model.GetPhotosService;
import com.flickrmap.flickrmap.model.VolleyWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by ron on 4/11/15.
 */
public class PhotosMapFragment extends com.google.android.gms.maps.MapFragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GetPhotosService.OnPhotosResultListener,
        AppPhotosIntents.OnDisplayAppPhotosListener {

    private static final String TAG = PhotosMapFragment.class.getSimpleName();

    public static final String EVENT_PHOTOS_ADDED   = TAG + ".EVENT_PHOTOS_ADDED";

    public static final String EVENT_PHOTOS_REMOVED = TAG + ".EVENT_PHOTOS_REMOVED";

    public static final String EVENT_PHOTOS_CLEARED = TAG + ".EVENT_PHOTOS_CLEARED";

    public static final int INITIAL_ZOOM = 12; // this should provide a ~15KM radius

    private GoogleApiClient mGoogleApiClient;

    private HashMap<String, AppPhotoDetailsImpl> mAppPhotosMap;

    // a receiver to handle result of get photos
    private BroadcastReceiver mPhotosResultsReceiver;

    // a receiver to handle requests to display a photo on the map
    private BroadcastReceiver mDisplayAppPhotoReceiver;


    /**
     * OnMapPhotoMarkersChangeListener used to handle changes in the photos that the map renders with markers
     */
    public interface OnMapPhotosChangeListener {

        void onMapPhotosAdded(Context context, Collection<AppPhotoDetails> photos);

        void onMapPhotosRemoved(Context context, Collection<AppPhotoDetails> photos);

        void onMapPhotosCleared(Context context);
    }


    private abstract static class OnMapPhotosChangeReceiver extends BroadcastReceiver implements OnMapPhotosChangeListener {

        @Override
        public final void onReceive(final Context context, final Intent intent) {

            String action = intent.getAction();
            if (EVENT_PHOTOS_CLEARED.equals(action)) {
                onMapPhotosCleared(context);
            }
            else {
                AppPhotosIntents.Parser parser = new AppPhotosIntents.Parser(intent.getExtras());
                Collection<AppPhotoDetails> photos = parser.getAppPhotosList();
                if (EVENT_PHOTOS_ADDED.equals(action)) {
                    onMapPhotosAdded(context, photos);
                }
                else if (EVENT_PHOTOS_REMOVED.equals(action)) {
                    onMapPhotosRemoved(context, photos);
                }
            }
        }
    }

    public static BroadcastReceiver registerOnMapPhotosChangeListener(final Context context,
                                                                      final OnMapPhotosChangeListener listener) {

        BroadcastReceiver receiver=null;
        if (listener!=null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(EVENT_PHOTOS_ADDED);
            filter.addAction(EVENT_PHOTOS_REMOVED);
            filter.addAction(EVENT_PHOTOS_CLEARED);
            receiver = new OnMapPhotosChangeReceiver() {

                @Override
                public void onMapPhotosAdded(final Context context, final Collection<AppPhotoDetails> photos) {
                    listener.onMapPhotosAdded(context,photos);
                }

                @Override
                public void onMapPhotosRemoved(final Context context, final Collection<AppPhotoDetails> photos) {
                    listener.onMapPhotosRemoved(context,photos);
                }

                @Override
                public void onMapPhotosCleared(final Context context) {
                    listener.onMapPhotosCleared(context);
                }
            };
            context.registerReceiver(receiver,filter);
        }
        return  receiver;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // connect the api-client
        // once the client connects  - start loading the photos for the current location.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onResume() {

        super.onResume();
        mGoogleApiClient.connect();

        // register a listener to handle retrieval of photos
        mPhotosResultsReceiver =
                GetPhotosService.registerOnPhotosResultListener(getActivity(), this);

        //register a listener to handle requests to display a photo on the map
        mDisplayAppPhotoReceiver =
                AppPhotosIntents.registerDisplayAppPhotosListener(getActivity(), this);

        //start loading the map
        getMapAsync(this);

    }

    @Override
    public void onPause() {

        super.onPause();
        mGoogleApiClient.disconnect();
        try {
            getActivity().unregisterReceiver(mDisplayAppPhotoReceiver);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getActivity().unregisterReceiver(mPhotosResultsReceiver);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            removeAllMarkers();
            Toast.makeText(getActivity(), R.string.feedback_marker_cleared, Toast.LENGTH_LONG).show();
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

    /* Google Api Client callbacks */

    @Override
    public void onConnected(Bundle bundle) {
        // hte google api client is connected - get current location and zoom to it
        zoomToLocation(getCurrentLocation());
        // now retrieve phot's details for this location
        retrievePhotosForLocation(getCurrentLocation());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(getActivity(), R.string.error_connecting_api_client, Toast.LENGTH_SHORT).show();

    }

    /* Map callbacks */

    @Override
    public boolean onMyLocationButtonClick() {

        retrievePhotosForLocation(getCurrentLocation());
        return false;
    }

    /**
     * implementing {@link com.flickrmap.flickrmap.model.GetPhotosService.OnPhotosResultListener}
     */
    @Override
    public void onPhotosResult(Context context, ArrayList<Photo> photos) {

        populatePhotosOnMap(photos);
        notifyPhotosAddedToMap();
    }

    @Override
    public void onFault(Context context) {

        Toast.makeText(getActivity(), R.string.error_photos_retrieve, Toast.LENGTH_SHORT).show();
    }

    /**
     * implementing {@link com.flickrmap.flickrmap.controller.AppPhotosIntents.OnDisplayAppPhotosListener}
     */
    @Override
    public void onDisplayAppPhoto(final Collection<AppPhotoDetails> photosDetails) {

        if (photosDetails != null && mAppPhotosMap != null) {
            for (AppPhotoDetails photoDetails : photosDetails) {

                AppPhotoDetailsImpl detailsImpl =
                        (AppPhotoDetailsImpl) mAppPhotosMap.get(photoDetails.getId());
                Marker mapMarker = detailsImpl.getMapMarker();
                if (mapMarker != null) {
                    mapMarker.showInfoWindow();
                    zoomToLocation(mapMarker.getPosition());
                }
            }
        }
    }

    private Location getCurrentLocation() {

        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void zoomToLocation(final Location location) {

        zoomToLocation(new LatLng(
                location.getLatitude(),
                location.getLongitude()));
    }

    private void zoomToLocation(final LatLng latLng) {

        GoogleMap googleMap = getMap();
        if (googleMap != null) {
            // Move the camera instantly tolocation with a zoom of 15.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM + 1));

            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM), 1000, null);
        }
    }


    private void retrievePhotosForLocation(Location location) {

        getActivity().startService(
                GetPhotosService
                        .createGetPhotosByLocationServiceIntent(
                                getActivity(),
                                location != null ?
                                        location :
                                        getCurrentLocation(),
                                100));
    }


    private void populatePhotosOnMap(ArrayList<Photo> photos) {

        // remove all markers
        removeAllMarkers();

        // re-populate makers
        mAppPhotosMap = new HashMap<String, AppPhotoDetailsImpl>(); // will clear older references
        int n = 0;
        for (Photo photo : photos) {
            try {
                GeoData geoData = photo.getGeoData();
                LatLng position = new LatLng(geoData.getLatitude(), geoData.getLongitude());
                MarkerOptions makerOptions = new MarkerOptions()
                        .position(position);
                Marker marker = getMap()
                        .addMarker(makerOptions);

                String markerId = marker.getId();
                AppPhotoDetailsImpl appPhotoDetails = createAppPhotoDetails(marker, photo);
                Log.v(TAG, "adding marker for: " + appPhotoDetails.toString() + " at: " +
                        position.toString());

                // keep a mapping from marker to photo-bundle so events identified by the marker's id can be related to a photo's details
                mAppPhotosMap.put(markerId, appPhotoDetails);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private AppPhotoDetailsImpl createAppPhotoDetails(Marker marker, Photo photo) {

        return AppPhotoDetailsImpl.create(marker.getId(),
                                          marker,
                                          photo.getSmallSquareUrl(), // should be 75x75
                                          photo.getMediumUrl(),
                                          photo.getTitle(),
                                          photo.getDescription());
    }


    private void removeAllMarkers() {

        if (mAppPhotosMap != null) {
            for (AppPhotoDetailsImpl appPhotoDetails : mAppPhotosMap.values()) {
                Marker marker = appPhotoDetails.getMapMarker();
                if (marker != null) {
                    marker.remove();
                }
            }
            notifyMapPhotosCleared();
        }
    }


    private void notifyPhotosAddedToMap(){
        AppPhotosIntents.Builder builder = new AppPhotosIntents.Builder(getActivity())
                .withAppPhotos(mAppPhotosMap.values());
        getActivity().sendBroadcast(builder.build().setAction(EVENT_PHOTOS_ADDED));
    }
    private void notifyMapPhotosCleared() {
        getActivity().sendBroadcast(new Intent(EVENT_PHOTOS_CLEARED));
    }

    /**
     * {@link AppPhotoDetailsImpl} - an internal implementation for the {@link com.flickrmap.flickrmap.controller.AppPhotoDetails} interface
     * the current activity creates and manages instances of this class.
     * <p/>
     * each instance is coupled to a maker on the map and mapped using {@link com.google.android.gms.maps.model.Marker :getId}.
     * the mapping takes place via {@value PhotosMapFragment:mAppPhotosMap}
     * <p/>
     * this instance also keeps a bitmap of the iamge if it was retrieved.
     */
    @AutoParcel
    static abstract class AppPhotoDetailsImpl implements AppPhotoDetails {

        Bitmap mLargeBitmap;

        Marker mMarker;

        public Bitmap getLargeBitmap() {

            return mLargeBitmap;
        }

        public void setLargeBitmap(@Nullable Bitmap largeBitmap) {

            mLargeBitmap = largeBitmap;
        }

        public void setMarker(final Marker marker) {

            mMarker = marker;
        }

        Marker getMapMarker() {

            return mMarker;
        }

        static AppPhotoDetailsImpl create(
                String markerId,
                Marker mapMarker,
                @Nullable String thumbnailUrl,
                @Nullable String largeUrl,
                @Nullable String title,
                @Nullable String description
        ) {

            AppPhotoDetailsImpl details =
                    new AutoParcel_PhotosMapFragment_AppPhotoDetailsImpl(markerId, thumbnailUrl, largeUrl, title,
                                                                         description);
            details.setMarker(mapMarker);
            return details;
        }
    }

    /**
     * {@link PhotoDetailsWindowAdapter} runs the adapter to match a marker on the map with a custom-info view
     */
    private class PhotoDetailsWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {

            return null; // default bubble-style
        }

        @Override
        public View getInfoContents(Marker marker) {

            final AppPhotoDetailsImpl photo =
                    mAppPhotosMap.get(marker.getId());
            View view = null;

            if (photo != null && photo.getLargeBitmap() != null) {
                view = LayoutInflater.from(getActivity())
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

            ImageRequest imageRequest = null;
            if (photo != null) {
                imageRequest = new ImageRequest(photo.getLargeSizeUrl(),
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
                VolleyWrapper.getInstance(getActivity())
                        .addToRequestQueue(imageRequest);
            }
            return imageRequest;

        }
    }
}
