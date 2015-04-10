package com.flickrmap.flickrmap.model.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.googlecode.flickrjandroid.photos.Photo;

/**
 * Created by ron on 4/10/15.
 */
public class FlickrPhotoWrapper implements Parcelable {
    Photo mPhoto;

    protected FlickrPhotoWrapper(Parcel in) {
        mPhoto = in.readByte() == 0x00 ?
                 null :
                 (Photo) in.readValue(Photo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mPhoto != null ?
                               0x01 :
                               0x00));
        if (mPhoto != null) {
            dest.writeValue(mPhoto);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FlickrPhotoWrapper> CREATOR =
            new Parcelable.Creator<FlickrPhotoWrapper>() {
                @Override
                public FlickrPhotoWrapper createFromParcel(Parcel in) {
                    return new FlickrPhotoWrapper(in);
                }

                @Override
                public FlickrPhotoWrapper[] newArray(int size) {
                    return new FlickrPhotoWrapper[ size ];
                }
            };
}