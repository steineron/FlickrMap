package com.flickrmap.flickrmap.view;

import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by ron on 4/10/15.
 * <p/>
 * an interface for encapsulation of details for one photo in the application
 * the interface extends {@link android.os.Parcelable}, which makes it simple to wrap it in a {@link android.os.Bundle}
 */

public interface AppPhotoDetails extends Parcelable {

    String getId();

    @Nullable
    String getThumbnailUrl();

    @Nullable
    String getLargeSizeUrl();

    @Nullable
    String getTitle();

    @Nullable
    String getDescription();

}
