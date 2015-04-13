package com.flickrmap.flickrmap.model;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.flickrmap.flickrmap.BuildConfig;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.googlecode.flickrjandroid.places.Place;
import com.googlecode.flickrjandroid.places.PlacesInterface;

import java.util.HashSet;

/**
 * an AsyncTask for retrieving Flickr images based on geo-location
 *
 * Created by ron on 4/9/15.
 */
class FlickrGeoSearchTask extends AsyncTask<Bundle, Void, PhotoList> {


    private static HashSet<String> mPhotoExtras;

    static {
        mPhotoExtras = new HashSet<>();
        mPhotoExtras.add("geo");
        mPhotoExtras.add("media");
        /*mPhotoExtras.add("url_sq");*/
        mPhotoExtras.add("url_t"); // thumbnail
        /*mPhotoExtras.add("url_s");
        mPhotoExtras.add("url_q");
        mPhotoExtras.add("url_m");
        mPhotoExtras.add("url_n");
        mPhotoExtras.add("url_z");
        mPhotoExtras.add("url_c");*/
        mPhotoExtras.add("url_l"); //large size
        /*mPhotoExtras.add("url_o");*/
        mPhotoExtras.add("original_format");
    }

    @Override
    protected PhotoList doInBackground(Bundle... params) {

        Bundle searchParams = params != null && params.length > 0 ?
                              params[ 0 ] :
                              null;

        PhotoList photos = new PhotoList();
        if (searchParams != null) {
            try {
                PhotosIntents.Parser parser = new PhotosIntents.Parser(searchParams);
                Location searchLocation = parser.getSearchLocation();
                int maxResults = parser.getMaxResults();
                Flickr flickr = new Flickr(BuildConfig.FLICKR_API_KEY);
                PhotosInterface photosInterface = flickr.getPhotosInterface();
                PlacesInterface placesInterface = flickr.getPlacesInterface();

                /*PlacesList places =
                        placesInterface.findByLatLon(searchLocation.getLatitude(), searchLocation.getLongitude(), Flickr.ACCURACY_STREET);

                for (Place place : places) {

                    photos.addAll( photosInterface.search(getSearchParameters(place), maxResults, 1) );
                }*/


                photos = photosInterface.search(getSearchParameters(searchLocation), maxResults, 1);

            } catch (Exception e) {
                e.printStackTrace();
                photos = null;
            }
        }
        return photos;

    }

    private SearchParameters getSearchParameters(final Place place) {

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setPlaceId(place.getPlaceId());
        searchParameters.setHasGeo(true);
//        searchParameters.setAccuracy(3);
        try {
            searchParameters.setMedia("photos");
        } catch (FlickrException e) {
            e.printStackTrace();
        }

        searchParameters.setExtras(mPhotoExtras);
        return searchParameters;
    }

    private SearchParameters getSearchParameters(Location searchLocation) {

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setLongitude(String.valueOf(searchLocation.getLongitude()));
        searchParameters.setLatitude(String.valueOf(searchLocation.getLatitude()));
        searchParameters.setHasGeo(true);
//        searchParameters.setAccuracy(3);
        try {
            searchParameters.setMedia("photos");
        } catch (FlickrException e) {
            e.printStackTrace();
        }

        searchParameters.setExtras(mPhotoExtras);
        return searchParameters;
    }
}
