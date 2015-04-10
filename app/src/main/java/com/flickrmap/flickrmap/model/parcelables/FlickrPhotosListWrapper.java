package com.flickrmap.flickrmap.model.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * Created by ron on 4/10/15.
 */
public class FlickrPhotosListWrapper implements Parcelable {


    PhotoList mPhotoList;

    public FlickrPhotosListWrapper(PhotoList mPhotoList) {
        this.mPhotoList = mPhotoList;
    }

    protected FlickrPhotosListWrapper(Parcel in) {
        mPhotoList = in.readByte() == 0x00 ?
                     null :
                     (PhotoList) in.readValue(PhotoList.class.getClassLoader());
    }

    public PhotoList getPhotoList() {
        return mPhotoList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mPhotoList != null ?
                               0x01 :
                               0x00));
        if (mPhotoList != null) {
            dest.writeValue(mPhotoList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FlickrPhotosListWrapper> CREATOR =
            new Parcelable.Creator<FlickrPhotosListWrapper>() {
                @Override
                public FlickrPhotosListWrapper createFromParcel(Parcel in) {
                    return new FlickrPhotosListWrapper(in);
                }

                @Override
                public FlickrPhotosListWrapper[] newArray(int size) {
                    return new FlickrPhotosListWrapper[ size ];
                }
            };
}