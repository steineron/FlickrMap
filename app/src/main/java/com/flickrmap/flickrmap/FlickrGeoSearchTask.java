package com.flickrmap.flickrmap;

import android.location.Location;
import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ron on 4/9/15.
 */
public class FlickrGeoSearchTask extends AsyncTask<Location, Void, PhotoList> {
    private static final java.lang.String FLICKER_API_KEY = "c58fa329327dc0bd3b0bb1b3954b5a37";

    @Override
    protected PhotoList doInBackground(Location... params) {

        Location searchLocation = params != null && params.length > 0 ? params[0] : null;

        PhotoList photos = null;
        if (searchLocation != null) {
            try {
                Flickr flickr = new Flickr(FLICKER_API_KEY);
                PhotosInterface photosInterface = flickr.getPhotosInterface();
                SearchParameters searchParameters = new SearchParameters();
                searchParameters.setLongitude(String.valueOf(searchLocation.getLongitude()));
                searchParameters.setLatitude(String.valueOf(searchLocation.getLatitude()));
                searchParameters.setHasGeo(true);

                Set<String> extras = new HashSet<>();
                extras.add("geo");
                extras.add("media");
                extras.add("url_sq");
                extras.add("url_t");
                extras.add("url_s");
                extras.add("url_q");
                extras.add("url_m");
                extras.add("url_n");
                extras.add("url_z");
                extras.add("url_c");
                extras.add("url_l");
                extras.add("url_o");
                extras.add("original_format");

                searchParameters.setExtras(extras);
                photos = photosInterface.search(searchParameters, 100, 1);
            } catch (Exception e) {
                e.printStackTrace();
                photos = null;
            }
        }
        return photos;

    }
}
