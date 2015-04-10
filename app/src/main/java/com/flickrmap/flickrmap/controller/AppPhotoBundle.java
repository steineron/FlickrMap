package com.flickrmap.flickrmap.controller;

import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by ron on 4/10/15.
 */
public interface AppPhotoBundle extends Parcelable {

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
