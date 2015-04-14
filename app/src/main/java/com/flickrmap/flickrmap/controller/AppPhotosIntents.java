package com.flickrmap.flickrmap.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * this class, and it's builder and parser, are used in the controller to communicate intents with {@link AppPhotoDetails}
 * <p/>
 * Created by ron on 4/11/15.
 */
public class AppPhotosIntents {

    private static final String TAG = AppPhotosIntents.class.getCanonicalName();

    public static final String ACTION_DISPLAY_PHOTO = TAG + ".ACTION_DISPLAY_PHOTO";

    public static final String EXTRA_PHOTOS_DETAILS_ARRAY = TAG + ".EXTRA_PHOTOS_DETAILS_ARRAY";

    public interface OnDisplayAppPhotosListener {

        void onDisplayAppPhoto(Collection<AppPhotoDetails> photoDetails);
    }

    private abstract static class DisplayAppPhotosReceiver extends BroadcastReceiver
            implements OnDisplayAppPhotosListener {

        @Override
        public final void onReceive(final Context context, final Intent intent) {

            if (ACTION_DISPLAY_PHOTO.equals(intent.getAction())) {
                onDisplayAppPhoto(new Parser(intent.getExtras())
                        .getAppPhotosList());
            }

        }
    }

    public static Intent createDisplayAppPhotoIntent(final Context context,
                                                     final AppPhotoDetails photoDetails) {

        ArrayList<AppPhotoDetails> list = new ArrayList<>();
        list.add(photoDetails);
        return createDisplayAppPhotosIntent(context,list);
    }

    public static Intent createDisplayAppPhotosIntent(final Context context,
                                                     final List<AppPhotoDetails> photoDetails) {

        return new Builder(context)
                .withAppPhotos(photoDetails)
                .build()
                .setAction(ACTION_DISPLAY_PHOTO);
    }

    public static BroadcastReceiver registerDisplayAppPhotosListener(final Context context,
                                                                     final OnDisplayAppPhotosListener listener) {

        BroadcastReceiver receiver = null;
        if (listener != null) {
            receiver = new DisplayAppPhotosReceiver() {

                @Override
                public void onDisplayAppPhoto(final Collection<AppPhotoDetails> photoDetails) {

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

        private Collection<? extends AppPhotoDetails> mPhotoDetails;

        public Builder(final Context context) {

            mContext = context;
        }

        public Builder withAppPhotos(Collection<? extends AppPhotoDetails> photoDetails) {

            mPhotoDetails = photoDetails;
            return this;
        }

        public Intent build() {

            ArrayList<Parcelable> all = new ArrayList<Parcelable>(mPhotoDetails.size());
            all.addAll(mPhotoDetails);
            return new Intent().putParcelableArrayListExtra(EXTRA_PHOTOS_DETAILS_ARRAY,
                                                            all);
        }
    }


    public static class Parser {

        final Bundle mExtras;

        public Parser(Bundle extras) {

            mExtras = extras;
        }

        public Collection<AppPhotoDetails> getAppPhotosList() {

            return mExtras == null ?
                    null :
                    mExtras.<AppPhotoDetails>getParcelableArrayList(EXTRA_PHOTOS_DETAILS_ARRAY);
        }
    }
}
