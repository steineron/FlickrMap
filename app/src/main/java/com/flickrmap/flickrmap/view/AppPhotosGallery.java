package com.flickrmap.flickrmap.view;

import com.flickrmap.flickrmap.controller.AppPhotoDetails;

import java.util.Collection;

/**
 * Created by rosteiner on 4/15/15.
 */
public interface AppPhotosGallery {

    public interface OnGalleryItemClickListener {

        void onGalleryItemClick(AppPhotoDetails photo);
    }

    void setAppPhotos(Collection<? extends AppPhotoDetails> photos);

    void setOnGalleryItemClickListener(OnGalleryItemClickListener listener);

}
