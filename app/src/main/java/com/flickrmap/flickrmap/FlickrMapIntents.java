package com.flickrmap.flickrmap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.flickrmap.flickrmap.controller.ControllerIntents;
import com.flickrmap.flickrmap.view.AppPhotoDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * the basic need is to encapsulate details of photos -
 * a collection of {@link com.flickrmap.flickrmap.view.AppPhotoDetails} inside an {@link android.content.Intent}
 * and be able to extract that collection from such Intent's extras {@link android.os.Bundle}
 * the {@link com.flickrmap.flickrmap.FlickrMapIntents.Builder} and {@link com.flickrmap.flickrmap.FlickrMapIntents.Parser}
 * classes do exactly that.
 * <p/>
 * Created by ron on 4/14/15.
 */
public final class FlickrMapIntents {

    protected static final String TAG = ControllerIntents.class.getCanonicalName();

    public static final String EXTRA_PHOTOS_DETAILS_ARRAY = TAG + ".EXTRA_PHOTOS_DETAILS_ARRAY";

    public static class Builder {


        private final Context mContext;

        private Collection<? extends AppPhotoDetails> mPhotoDetails;

        public Builder(final Context context) {

            mContext = context;
        }

        public FlickrMapIntents.Builder withAppPhotos(
                Collection<? extends AppPhotoDetails> photoDetails) {

            mPhotoDetails = photoDetails;
            return this;
        }

        public Intent build() {

            ArrayList<Parcelable> all = mPhotoDetails == null ?
                                        null :
                                        new ArrayList<Parcelable>(mPhotoDetails);
            return new Intent().putParcelableArrayListExtra(
                    EXTRA_PHOTOS_DETAILS_ARRAY,
                    all);
        }
    }

    public static class Parser {

        final Bundle mExtras;

        public Parser(Bundle extras) {

            mExtras = extras;
        }

        public Collection<AppPhotoDetails> getAppPhotosList() {

            return mExtras == null ?
                   null :
                   mExtras.<AppPhotoDetails>getParcelableArrayList(EXTRA_PHOTOS_DETAILS_ARRAY);
        }
    }
}
