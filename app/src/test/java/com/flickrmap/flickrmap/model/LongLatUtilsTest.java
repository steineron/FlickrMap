package com.flickrmap.flickrmap.model;

import junit.framework.TestCase;

public class LongLatUtilsTest extends TestCase {

    public void test_that_calculated_distance_for_same_location_is_zero() throws Exception {

        double lat = 34.123;
        double lng = 35.321;

        assertEquals(0.0, LongLatUtils.calculateDistance(lat, lng, lat, lng));
    }

    public void test_that_calculated_distance_is_associative() throws Exception {

        double lat1 = 34.123;
        double lng1 = 35.321;
        double lat2 = 35.123;
        double lng2 = 34.321;

        assertEquals(LongLatUtils.calculateDistance(lat1, lng1, lat2, lng2), LongLatUtils.calculateDistance(lat2, lng2, lat1, lng1));
    }
}