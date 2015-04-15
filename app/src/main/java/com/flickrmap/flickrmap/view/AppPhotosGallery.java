package com.flickrmap.flickrmap.view;

import java.util.Collection;

/**
 * interface for views that display a collection of {@link com.flickrmap.flickrmap.view.AppPhotoDetails}
 * in a gallery-like fashion.
 *
 * Created by rosteiner on 4/15/15.
 */
public interface AppPhotosGallery {

    public interface OnGalleryItemClickListener {

        void onGalleryItemClick(AppPhotoDetails photo);
    }

    void setAppPhotos(Collection<? extends AppPhotoDetails> photos);

    void setOnGalleryItemClickListener(OnGalleryItemClickListener listener);

}
