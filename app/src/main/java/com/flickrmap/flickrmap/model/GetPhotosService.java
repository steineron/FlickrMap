package com.flickrmap.flickrmap.model;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.flickrmap.flickrmap.model.parcelables.FlickrPhotosListWrapper;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import java.util.ArrayList;

/**
 *
 * the service that manages the task to retrieve images for the application
 *
 * Created by ron on 4/10/15.
 */
public class GetPhotosService extends Service {

    private static final String TAG = GetPhotosService.class.getSimpleName();

    private static final String ACTION_GET_PHOTOS = TAG + ".ACTION_GET_PHOTOS";

    private static final String ACTION_GET_PHOTOS_RESULT = TAG + ".ACTION_GET_PHOTOS_RESULT";

    private static final String EXTRA_RESULT_PHOTOS = TAG + ".EXTRA_RESULT_PHOTOS";

    private static final String EXTRA_SUCCESS = TAG + ".EXTRA_SUCCESS";


    public interface OnPhotosResultListener extends OnServiceResultListener {

        void onPhotosResult(Context context, ArrayList<Photo> photos);
    }


    // create an intent to start this service with location and page size parameters
    public static Intent createGetPhotosByLocationServiceIntent(Context context, Location location,
                                                                int maxPhotos) {

        return new GetPhotosIntents.Builder(context)
                .withMexResults(maxPhotos)
                .withSearchLocation(location)
                .build()
                .setClass(context, GetPhotosService.class)
                .setAction(ACTION_GET_PHOTOS);

    }

    // register a listener to teh result (success/ fault) of the retrieval task
    public static BroadcastReceiver registerOnPhotosResultListener(final Context context,
                                                                   final OnPhotosResultListener listener) {

        BroadcastReceiver receiver =
                listener == null ?
                null :
                new OnPhotosResultReceiver() {

                    @Override
                    public void onPhotosResult(Context context,
                                               ArrayList<Photo> photos) {

                        listener.onPhotosResult(context, photos);
                    }

                    @Override
                    public void onFault(Context context) {

                        listener.onFault(context);
                    }
                };
        if (receiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_GET_PHOTOS_RESULT);
            context.registerReceiver(receiver, intentFilter);
        }
        return receiver;
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        if (intent != null) {
            FlickrGeoSearchTask getPhotosTask = new FlickrGeoSearchTask() {

                @Override
                protected void onPostExecute(PhotoList photos) {

                    super.onPostExecute(photos);
                    sendBroadcast(new Intent(ACTION_GET_PHOTOS_RESULT)
                            .putExtra(EXTRA_SUCCESS, photos != null)
                            .putExtra(EXTRA_RESULT_PHOTOS, new FlickrPhotosListWrapper(photos)));
                    stopSelf(startId);
                }
            };
            getPhotosTask.execute(new Bundle[]{intent.getExtras()});
        }
        return super.onStartCommand(intent, flags, startId);
    }

    abstract static class OnPhotosResultReceiver extends BroadcastReceiver implements OnPhotosResultListener {

        @Override
        public final void onReceive(Context context, Intent intent) {

            if (intent.getBooleanExtra(EXTRA_SUCCESS, false)) {
                onPhotosResult(context, ((FlickrPhotosListWrapper) intent.getParcelableExtra(EXTRA_RESULT_PHOTOS)).getPhotoList());
            }
            else {
                onFault(context);
            }
        }

    }
}
