package com.flickrmap.flickrmap.controller.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.flickrmap.flickrmap.R;
import com.flickrmap.flickrmap.controller.AppPhotoBundle;
import com.flickrmap.flickrmap.model.VolleyWrapper;
import com.flickrmap.flickrmap.view.HorizontalSpaceDecorator;

import java.util.ArrayList;

/**
 * A  {@link Fragment} subclass that display a list of images in a horizontal gallery.
 * interaction events broadcast a.
 * Use the {@link PhotoGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoGalleryFragment extends Fragment {


    private HorizontalGridView mGalleryView;
    private ArrayList<AppPhotoBundle> mPhotosList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param photos - an array list of {@link AppPhotoBundle} that owns the details about the photos this gallery fragment displays
     * @return A new instance of fragment PhotoGalleryFragment.
     */
    public static PhotoGalleryFragment newInstance(ArrayList<AppPhotoBundle> photos) {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setPhotosList(photos);
        return fragment;
    }

    public void setPhotosList(ArrayList<AppPhotoBundle> photos) {

        mPhotosList = photos;
    }

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mGalleryView = (HorizontalGridView) view.findViewById(R.id.thumbs_gallery);
        mGalleryView.addItemDecoration(new HorizontalSpaceDecorator(getResources().getInteger(R.integer.gallery_space_half_size)));
        mGalleryView.setAdapter(new PhotoGalleryAdapter());

    }

    private class GalleryViewHolder extends RecyclerView.ViewHolder {

        protected final ImageView mImageView;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.thumb_image);
        }
    }

    private class PhotoGalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

        @Override
        public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GalleryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.gallery_thumb_view,
                            parent,
                            false));
        }

        @Override
        public void onBindViewHolder(final GalleryViewHolder holder, int position) {


            AppPhotoBundle photo = null;
            try {
                photo = mPhotosList.get(position);
            } catch (Exception e) {
                photo = null;
            }

            if (photo != null) {
                // Retrieves an image specified by the URL, displays it in the UI.
                ImageRequest request = new ImageRequest(photo.getThumbnailUrl(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                holder.mImageView.setImageBitmap(bitmap);
                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                holder.mImageView.setImageResource(/*R.drawable.image_load_error*/ 0);
                            }
                        });
                VolleyWrapper.getInstance(getActivity())
                        .addToRequestQueue(request);
            }

        }


        @Override
        public int getItemCount() {
            return mPhotosList == null ?
                   0 :
                   mPhotosList.size();
        }
    }
}
