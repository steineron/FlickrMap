package com.flickrmap.flickrmap.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * this class, and it's builder and parser, are used in the controller to communicate intents with {@link AppPhotoDetails}
 * <p/>
 * Created by ron on 4/11/15.
 */
public class AppPhotoDetailsIntent {

    private static final String TAG = AppPhotoDetailsIntent.class.getCanonicalName();

    public static final String ACTION_DISPLAY_PHOTO = TAG + ".ACTION_DISPLAY_PHOTO";

    public static final String EXTRA_PHOTO_DETAILS = TAG + ".EXTRA_PHOTO_DETAILS";

    public interface OnDisplayAppPhotoListener {

        void onDisplayAppPhoto(AppPhotoDetails photoDetails);
    }

    private abstract static class DisplayAppPhotoReceiver extends BroadcastReceiver implements OnDisplayAppPhotoListener {

        @Override
        public final void onReceive(final Context context, final Intent intent) {

            if (ACTION_DISPLAY_PHOTO.equals(intent.getAction())) {
                onDisplayAppPhoto(new Parser(intent.getExtras())
                        .getAppPhotoDetails());
            }

        }
    }

    public static Intent createDisplayAppPhotoIntent(final Context context,
                                                     final AppPhotoDetails photoDetails) {

        return new Builder(context)
                .withAppPhotoDetails(photoDetails)
                .build()
                .setAction(ACTION_DISPLAY_PHOTO);
    }

    public static BroadcastReceiver registerDisplayAppPhotoListener(final Context context,
                                                                    final OnDisplayAppPhotoListener listener) {

        BroadcastReceiver receiver = null;
        if (listener != null) {
            receiver = new DisplayAppPhotoReceiver() {

                @Override
                public void onDisplayAppPhoto(final AppPhotoDetails photoDetails) {

                    listener.onDisplayAppPhoto(photoDetails);
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DISPLAY_PHOTO);
            context.registerReceiver(receiver, filter);
        }
        return receiver;
    }

    public static class Builder {

        private final Context mContext;

        private AppPhotoDetails mPhotoDetails;

        public Builder(final Context context) {

            mContext = context;
        }

        Builder withAppPhotoDetails(AppPhotoDetails photoDetails) {

            mPhotoDetails = photoDetails;
            return this;
        }

        public Intent build() {

            return new Intent().putExtra(EXTRA_PHOTO_DETAILS, mPhotoDetails);
        }
    }


    public static class Parser {

        final Bundle mExtras;

        public Parser(Bundle extras) {

            mExtras = extras;
        }

        AppPhotoDetails getAppPhotoDetails() {

            return mExtras == null ?
                   null :
                   (AppPhotoDetails) mExtras.getParcelable(EXTRA_PHOTO_DETAILS);
        }
    }
}
