package com.flickrmap.flickrmap.controller.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flickrmap.flickrmap.R;
import com.flickrmap.flickrmap.controller.ControllerIntents;
import com.flickrmap.flickrmap.view.AppPhotoDetails;
import com.flickrmap.flickrmap.view.AppPhotosGallery;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A  {@link Fragment} subclass that display a list of images in a gallery.
 * interaction events broadcast a.
 * Use the {@link PhotoGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoGalleryFragment extends Fragment implements PhotosMapFragment.OnMapPhotosChangeListener,
                                                              AppPhotosGallery.OnGalleryItemClickListener {


    // the gallery renderer/view
    private AppPhotosGallery mPhotosGallery;

    // the list of photo details
    private ArrayList<AppPhotoDetails> mPhotosList;

    // listener to changes in the photos on the map (added/ cleared)
    private BroadcastReceiver mMapPhotosChangedReceiver;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param photos - an array list of {@link com.flickrmap.flickrmap.view.AppPhotoDetails} that owns the details about the photos this gallery fragment displays
     * @return A new instance of fragment PhotoGalleryFragment.
     */
    public static PhotoGalleryFragment newInstance(Collection<AppPhotoDetails> photos) {

        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setPhotosList(photos);
        return fragment;
    }

    public void setPhotosList(Collection<AppPhotoDetails> photos) {

        mPhotosList = new ArrayList<>(photos);
    }

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mMapPhotosChangedReceiver =
                PhotosMapFragment.registerOnMapPhotosChangeListener(getActivity(), this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        mPhotosGallery = (AppPhotosGallery) view.findViewById(R.id.thumbs_gallery);
        mPhotosGallery.setOnGalleryItemClickListener(this);
        mPhotosGallery.setAppPhotos(mPhotosList);
    }

    @Override
    public void onDestroy() {

        try {
            getActivity().unregisterReceiver(mMapPhotosChangedReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    public void onMapPhotosAdded(final Context context, final Collection<AppPhotoDetails> photos) {

        setPhotosList(photos);
        if (mPhotosGallery != null) {
            mPhotosGallery.setAppPhotos(mPhotosList);
        }
    }

    @Override
    public void onMapPhotosCleared(final Context context) {

        if (mPhotosGallery != null) {
            mPhotosGallery.setAppPhotos(null);
        }
    }


    @Override
    public void onGalleryItemClick(final AppPhotoDetails photo) {

        Activity activity = getActivity();
        activity.sendBroadcast(ControllerIntents
                .createDisplayAppPhotoIntent(activity, photo));
    }
}
