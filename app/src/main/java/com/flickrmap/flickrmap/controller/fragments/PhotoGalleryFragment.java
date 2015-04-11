package com.flickrmap.flickrmap.controller.fragments;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.flickrmap.flickrmap.R;
import com.flickrmap.flickrmap.controller.AppPhotoDetails;
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


    private RecyclerView mGalleryView;

    private ArrayList<AppPhotoDetails> mPhotosList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param photos - an array list of {@link com.flickrmap.flickrmap.controller.AppPhotoDetails} that owns the details about the photos this gallery fragment displays
     * @return A new instance of fragment PhotoGalleryFragment.
     */
    public static PhotoGalleryFragment newInstance(ArrayList<AppPhotoDetails> photos) {

        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setPhotosList(photos);
        return fragment;
    }

    public void setPhotosList(ArrayList<AppPhotoDetails> photos) {

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
        mGalleryView = (RecyclerView) view.findViewById(R.id.thumbs_gallery);
        mGalleryView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mGalleryView.setLayoutManager(layoutManager);
        mGalleryView.addItemDecoration(new HorizontalSpaceDecorator(getResources().getInteger(R.integer.gallery_space_half_size)));
        mGalleryView.setAdapter(new PhotoGalleryAdapter());

    }

    private class GalleryViewHolder extends RecyclerView.ViewHolder {

        protected final ImageView mImageView;

        protected ImageRequest mImageRequest;

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

            AppPhotoDetails photo = null;
            try {
                photo = mPhotosList.get(position);
            } catch (Exception e) {
                photo = null;
            }
            holder.mImageView.setBackground(/*R.drawable.image_load_error*/ null);
            holder.mImageView.setVisibility(View.INVISIBLE);
            if (photo != null) {
                // Retrieves an image specified by the URL, displays it in the UI.
                holder.mImageRequest = new ImageRequest(photo.getThumbnailUrl(),
                        new Response.Listener<Bitmap>() {

                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onResponse(Bitmap bitmap) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                                        holder.mImageView.isAttachedToWindow()) {
                                    revealView(holder.mImageView);
                                }
                                holder.mImageView.setImageBitmap(bitmap);
                                holder.mImageView.setVisibility(View.VISIBLE);
                            }

                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                        new Response.ErrorListener() {

                            public void onErrorResponse(VolleyError error) {

                                //TODO: handle the error properly
                                holder.mImageView.setImageBitmap(/*R.drawable.image_load_error*/ null);
                            }
                        });
                // launch the request
                VolleyWrapper.getInstance(getActivity())
                        .addToRequestQueue(holder.mImageRequest);
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                    }
                });
            }

        }

        @Override
        public void onViewRecycled(GalleryViewHolder holder) {

            holder.mImageView.setImageBitmap(null);
            holder.mImageRequest.cancel();
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {

            return mPhotosList == null ?
                   0 :
                   mPhotosList.size();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealView(View view) {
        // this groovy effect requires Android 5.0 and above

        if (view != null) {
            float radius =
                    Math.max(view.getMeasuredHeight(), view.getMeasuredWidth());

            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    view,           // The  View to reveal
                    Math.round(
                            radius *
                                    0.5f),      // x to start the mask from - start from the middle
                    Math.round(
                            radius),      // y to start the mask from - start from the bottom
                    0f,                          // radius of the starting mask
                    radius);                     // radius of the final mask
            reveal.setDuration(150L)
                    .setInterpolator(AnimationUtils.loadInterpolator(getActivity(),
                            android.R.interpolator.linear_out_slow_in));
            reveal.start();
        }
    }
}
