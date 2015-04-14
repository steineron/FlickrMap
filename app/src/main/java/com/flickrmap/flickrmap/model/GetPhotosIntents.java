package com.flickrmap.flickrmap.model;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

/**
 * handles the task of building/parsing intents for getting photos
 * <p/>
 * Created by ron on 4/10/15.
 */
final class GetPhotosIntents {

    private static final String TAG = GetPhotosIntents.class.getCanonicalName();

    // use canonical name to be very specific with the extras' key names
    private static final String EXTRA_LOCATION = TAG + ".EXTRA_LOCATION";

    private static final String EXTRA_MAX_RESULTS = TAG + ".EXTRA_MAX_RESULTS";

    public static class Builder {

        private final Context mContext;

        private Location mLocation;

        private int mMaxResults;

        public Builder(Context context) {

            mContext = context;
        }

        public Builder withSearchLocation(Location location) {

            mLocation = location;
            return this;
        }

        public Builder withMexResults(int maxResults) {

            mMaxResults = maxResults;
            return this;
        }

        public Intent build() {

            return new Intent()
                    .putExtra(EXTRA_LOCATION, mLocation)
                    .putExtra(EXTRA_MAX_RESULTS, mMaxResults);
        }
    }

    public static class Parser {

        private final Bundle mExtras;

        public Parser(Bundle extras) {

            mExtras = extras;
        }

        public Location getSearchLocation() {

            return mExtras == null ?
                   null :
                   (Location) mExtras.getParcelable(EXTRA_LOCATION);
        }

        public int getMaxResults() {

            return mExtras == null ?
                   -1 :
                   mExtras.getInt(EXTRA_MAX_RESULTS, -1);
        }

    }
}
