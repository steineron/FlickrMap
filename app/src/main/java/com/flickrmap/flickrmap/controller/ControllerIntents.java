package com.flickrmap.flickrmap.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.flickrmap.flickrmap.FlickrMapIntents;
import com.flickrmap.flickrmap.view.AppPhotoDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * this class, are used in the controller to communicate events and actions using
 * intents with {@link com.flickrmap.flickrmap.view.AppPhotoDetails}
 * <p/>
 * Created by ron on 4/11/15.
 */
public final class ControllerIntents {

    private static final String TAG = ControllerIntents.class.getSimpleName();

    /**
     * Display a photo:
     */
    public static final String ACTION_DISPLAY_PHOTO = TAG + ".ACTION_DISPLAY_PHOTO";

    public interface OnDisplayAppPhotoListener {

        void onDisplayAppPhoto(final Context context, Collection<AppPhotoDetails> photoDetails);
    }

    private abstract static class DisplayAppPhotoReceiver extends BroadcastReceiver
            implements OnDisplayAppPhotoListener {

        @Override
        public final void onReceive(final Context context, final Intent intent) {

            if (ACTION_DISPLAY_PHOTO.equals(intent.getAction())) {
                onDisplayAppPhoto(context, new FlickrMapIntents.Parser(intent.getExtras())
                        .getAppPhotosList());
            }

        }
    }

    public static Intent createDisplayAppPhotoIntent(final Context context,
                                                     final AppPhotoDetails photoDetails) {

        ArrayList<AppPhotoDetails> list = new ArrayList<>();
        list.add(photoDetails);
        return createDisplayAppPhotosIntent(context, list);
    }

    public static Intent createDisplayAppPhotosIntent(final Context context,
                                                      final List<AppPhotoDetails> photoDetails) {

        return new FlickrMapIntents.Builder(context)
                .withAppPhotos(photoDetails)
                .build()
                .setAction(ACTION_DISPLAY_PHOTO);
    }

    public static BroadcastReceiver registerDisplayAppPhotosListener(final Context context,
                                                                     final OnDisplayAppPhotoListener listener) {

        BroadcastReceiver receiver = null;
        if (listener != null) {
            receiver = new DisplayAppPhotoReceiver() {

                @Override
                public void onDisplayAppPhoto(final Context context,
                                              final Collection<AppPhotoDetails> photoDetails) {

                    listener.onDisplayAppPhoto(context, photoDetails);
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DISPLAY_PHOTO);
            context.registerReceiver(receiver, filter);
        }
        return receiver;
    }


    /**
     * Clear all photos:
     */

    public static final String ACTION_CLEAR_ALL = TAG + ".ACTION_CLEAR_ALL";

    public interface OnClearAllPhotosListener {

        void onClearAllPhotos(final Context context);
    }

    private abstract static class ClearAllPhotosReceiver extends BroadcastReceiver
            implements OnClearAllPhotosListener {

        @Override
        public final void onReceive(final Context context, final Intent intent) {

            if (ACTION_CLEAR_ALL.equals(intent.getAction())) {
                onClearAllPhotos(context);
            }

        }
    }


    public static Intent createClearAllPhotosIntent(final Context context) {

        return new FlickrMapIntents.Builder(context)
                .build()
                .setAction(ACTION_CLEAR_ALL);
    }

    public static BroadcastReceiver registerClearAllPhotosListener(final Context context,
                                                                   final OnClearAllPhotosListener listener) {

        BroadcastReceiver receiver = null;
        if (listener != null) {
            receiver = new ClearAllPhotosReceiver() {

                @Override
                public void onClearAllPhotos(final Context context) {

                    listener.onClearAllPhotos(context);
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_CLEAR_ALL);
            context.registerReceiver(receiver, filter);
        }
        return receiver;
    }


}
