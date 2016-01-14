package com.bromancelabs.locatr.fragments;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bromancelabs.locatr.R;
import com.bromancelabs.locatr.models.Photo;
import com.bromancelabs.locatr.models.PhotosObject;
import com.bromancelabs.locatr.services.FlickrService;
import com.bromancelabs.locatr.services.RetrofitSingleton;
import com.bromancelabs.locatr.utils.DialogUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

public class LocatrFragment extends Fragment {
    private static final String TAG = LocatrFragment.class.getSimpleName();
    private static final String URL = "https://api.flickr.com/services/rest/";
    private static final String FLICKR_API_KEY = "b71c3d2d57d035bf593c78dcb4b659d1";
    private static final String FLICKR_API_SEARCH_PHOTOS = "flickr.photos.search";
    private static final String FLICKR_API_FORMAT = "json";
    private static final String FLICKR_API_JSON_CALLBACK = "1";
    private static final String FLICKR_API_EXTRAS = "url_s";

    @Bind(R.id.iv_image) ImageView mImageView;

    private GoogleApiClient mGoogleClient;

    private FlickrService mFlickrService;

    private Dialog mProgressDialog;

    private List<Photo> mPhotoList;

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        buildGoogleApiClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_locatr, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleClient.connect();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleClient.disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        menu.findItem(R.id.action_locate).setEnabled(mGoogleClient.isConnected());
    }

    private void getFlickrPhotos(Location location) {
        mProgressDialog = DialogUtils.showProgressDialog(getActivity());
        mFlickrService = RetrofitSingleton.getInstance(URL).create(FlickrService.class);

        String searchString = "dogs";

        mFlickrService.searchPhotos(FLICKR_API_SEARCH_PHOTOS, FLICKR_API_KEY, FLICKR_API_FORMAT, FLICKR_API_JSON_CALLBACK, searchString, location.getLatitude(), location.getLongitude(), FLICKR_API_EXTRAS).enqueue(new Callback<PhotosObject>() {
            @Override
            public void onResponse(Response<PhotosObject> response) {
                if (response.isSuccess()) {
                    final long responseSize = Long.parseLong(response.body().getPhotos().getTotal());
                    Log.d(TAG, "JSON response # of photos: " + responseSize);
                    mPhotoList = response.body().getPhotos().getPhoto();
                } else {
                    Log.e(TAG, "Error: " + response.message());
                }
                dismissDialog(mProgressDialog);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error: " + t.toString());
                dismissDialog(mProgressDialog);
            }
        });
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void buildGoogleApiClient() {
        mGoogleClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
    }
}
