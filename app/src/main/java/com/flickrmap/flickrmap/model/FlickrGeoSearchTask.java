package com.flickrmap.flickrmap.model;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import java.util.HashSet;

/**
 * Created by ron on 4/9/15.
 */
class FlickrGeoSearchTask extends AsyncTask<Bundle, Void, PhotoList> {
    private static final java.lang.String FLICKER_API_KEY = "c58fa329327dc0bd3b0bb1b3954b5a37";

    private static HashSet<String> mPhotoExtras;

    static {
        mPhotoExtras = new HashSet<>();
        mPhotoExtras.add("geo");
        mPhotoExtras.add("media");
        mPhotoExtras.add("url_sq");
        mPhotoExtras.add("url_t");
        mPhotoExtras.add("url_s");
        mPhotoExtras.add("url_q");
        mPhotoExtras.add("url_m");
        mPhotoExtras.add("url_n");
        mPhotoExtras.add("url_z");
        mPhotoExtras.add("url_c");
        mPhotoExtras.add("url_l");
        mPhotoExtras.add("url_o");
        mPhotoExtras.add("original_format");
    }

    @Override
    protected PhotoList doInBackground(Bundle... params) {

        Bundle searchParams = params != null && params.length > 0 ?
                              params[ 0 ] :
                              null;

        PhotoList photos = null;
        if (searchParams != null) {
            try {
                PhotosIntents.Parser parser = new PhotosIntents.Parser(searchParams);
                Location searchLocation = parser.getSearchLocation();
                int maxResults = parser.getMaxResults();
                Flickr flickr = new Flickr(FLICKER_API_KEY);
                PhotosInterface photosInterface = flickr.getPhotosInterface();

                photos = photosInterface.search(getSearchParameters(searchLocation), maxResults, 1);
            } catch (Exception e) {
                e.printStackTrace();
                photos = null;
            }
        }
        return photos;

    }

    private SearchParameters getSearchParameters(Location searchLocation) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setLongitude(String.valueOf(searchLocation.getLongitude()));
        searchParameters.setLatitude(String.valueOf(searchLocation.getLatitude()));
        searchParameters.setHasGeo(true);
        searchParameters.setExtras(mPhotoExtras);
        return searchParameters;
    }
}
