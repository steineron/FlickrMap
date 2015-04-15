package com.flickrmap.flickrmap.view;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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
import com.flickrmap.flickrmap.model.VolleyWrapper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rosteiner on 4/15/15.
 */
public class HorizontalThumbsGalleryView extends RecyclerView implements AppPhotosGallery {

    private ArrayList<AppPhotoDetails> mPhotosList;

    private OnGalleryItemClickListener mOnGalleryItemClickListener;

    public HorizontalThumbsGalleryView(final Context context) {

        super(context);
    }

    public HorizontalThumbsGalleryView(final Context context, final AttributeSet attrs) {

        super(context, attrs);
    }

    public HorizontalThumbsGalleryView(final Context context, final AttributeSet attrs,
                                       final int defStyle) {

        super(context, attrs, defStyle);
    }


    @Override
    public void setAppPhotos(final Collection<? extends AppPhotoDetails> photos) {

        mPhotosList = photos == null ?
                      null :
                      new ArrayList<>(photos);
        setAdapter(new PhotoGalleryAdapter());
    }

    @Override
    public void setOnGalleryItemClickListener(final OnGalleryItemClickListener listener) {

        mOnGalleryItemClickListener = listener;
    }

    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();
        setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);
        addItemDecoration(new HorizontalSpaceDecorator(getResources().getInteger(R.integer.gallery_space_half_size)));
        setAdapter(new PhotoGalleryAdapter());
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

            final GalleryViewHolder galleryViewHolder =
                    new GalleryViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(
                                    R.layout.gallery_thumb_view,
                                    parent,
                                    false));
            galleryViewHolder.mImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {

                    if (mOnGalleryItemClickListener != null) {
                        mOnGalleryItemClickListener.onGalleryItemClick(
                                mPhotosList.get(
                                        galleryViewHolder
                                                .getAdapterPosition()));
                    }

                }
            });
            return galleryViewHolder;
        }

        @Override
        public void onBindViewHolder(final GalleryViewHolder holder, int position) {

            try {
                final AppPhotoDetails photo = mPhotosList.get(position);

                holder.mImageView.setImageBitmap(/*R.drawable.image_load_error*/ null);
                holder.itemView.setVisibility(INVISIBLE);
                if (photo != null) {
                    // Retrieves an image specified by the URL, displays it in the UI.
                    holder.mImageRequest =
                            new ImageRequest(photo.getThumbnailUrl(),
                                    new Response.Listener<Bitmap>() {

                                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void onResponse(Bitmap bitmap) {

                                            holder.itemView.setVisibility(VISIBLE);
                                            if (Build.VERSION.SDK_INT >=
                                                    Build.VERSION_CODES.LOLLIPOP &&
                                                    holder.mImageView.isAttachedToWindow()) {
                                                revealView(holder.itemView);
                                            }
                                            holder.mImageView.setImageBitmap(bitmap);
                                        }

                                    }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                                    new Response.ErrorListener() {

                                        public void onErrorResponse(VolleyError error) {

                                            //TODO: handle the error properly
                                            holder.mImageView.setImageBitmap(/*R.drawable.image_load_error*/
                                                    null);
                                        }
                                    }
                            );

                    // launch the request
                    VolleyWrapper.getInstance(getContext())
                            .addToRequestQueue(holder.mImageRequest);

                }
            } catch (Exception e) {

            }
            AppPhotoDetails photo = null;

        }

        @Override
        public void onViewRecycled(GalleryViewHolder holder) {

            holder.mImageView.setImageBitmap(null);
            holder.mImageRequest.cancel();
            holder.itemView.setVisibility(INVISIBLE);
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
                    .setInterpolator(AnimationUtils.loadInterpolator(getContext(),
                            android.R.interpolator.linear_out_slow_in));
            reveal.start();
        }
    }

    /**
     * Created by ron on 4/10/15.
     */
    public static class HorizontalSpaceDecorator extends ItemDecoration {

        private int space;

        /**
         * @param space - HALF the space to use when decorating the offsets.
         */
        public HorizontalSpaceDecorator(int space) {

            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   State state) {
            // Add left margin only for the first item to avoid double space between items
            outRect.left = parent.getChildPosition(view) == 0 ?
                           2 * space :
                           space;
            // Add right margin only for the last item to avoid double space between items
            outRect.right = parent.getChildPosition(view) == parent.getChildCount() - 1 ?
                            2 * space :
                            space;
            outRect.bottom = 2 * space;

            outRect.top = 2 * space;
        }

    }
}
