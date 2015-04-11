package com.flickrmap.flickrmap.controller;

import android.content.Intent;
import android.os.Bundle;

/**
 * this class, and it's builder and parser, are used in the controller to communicate intents with {@link AppPhotoDetails}
 * <p/>
 * Created by ron on 4/11/15.
 */
public class AppPhotoDetailsIntent {

    private static final String TAG = AppPhotoDetailsIntent.class.getCanonicalName();

    public static final String EXTRA_PHOTO_DETAILS = TAG + ".EXTRA_PHOTO_DETAILS";

    public static class Builder {

        private AppPhotoDetails mPhotoDetails;

        public Builder() {

        }

        Builder withAppPhotoDetails(AppPhotoDetails photoDetails) {

            mPhotoDetails = photoDetails;
            return this;
        }

        public Intent build() {

            return new Intent().putExtra(EXTRA_PHOTO_DETAILS, mPhotoDetails);
        }
    }


    public static class Parser {

        final Bundle mExtras;

        public Parser(Bundle extras) {

            mExtras = extras;
        }

        AppPhotoDetails getAppPhotoDetails() {

            return mExtras == null ?
                   null :
                   (AppPhotoDetails) mExtras.getParcelable(EXTRA_PHOTO_DETAILS);
        }
    }
}
