package com.flickrmap.flickrmap.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * a utility class to calculate distances between two locations given as Long/Lat coordinates.
 * the calculation is in meters
 * Created by rosteiner on 4/13/15.
 */
public final class LongLatUtils {

    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }

    public static double calculateDistance(LatLng position1, LatLng position2) {

        return calculateDistance(position1.latitude, position1.longitude,
                                 position2.latitude, position2.longitude);
    }

}
