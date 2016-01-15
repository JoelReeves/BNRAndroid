package com.bromancelabs.locatr.fragments;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bromancelabs.locatr.R;
import com.bromancelabs.locatr.models.Photo;
import com.bromancelabs.locatr.models.PhotosObject;
import com.bromancelabs.locatr.services.FlickrService;
import com.bromancelabs.locatr.services.RetrofitSingleton;
import com.bromancelabs.locatr.utils.DialogUtils;
import com.bromancelabs.locatr.utils.NetworkUtils;
import com.bromancelabs.locatr.utils.SnackBarUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocatrFragment extends SupportMapFragment {
    private static final String TAG = LocatrFragment.class.getSimpleName();
    private static final String URL = "https://api.flickr.com/services/rest/";
    private static final String FLICKR_API_KEY = "b71c3d2d57d035bf593c78dcb4b659d1";
    private static final String FLICKR_API_SEARCH_PHOTOS = "flickr.photos.search";
    private static final String FLICKR_API_FORMAT = "json";
    private static final String FLICKR_API_JSON_CALLBACK = "1";
    private static final String FLICKR_API_EXTRAS = "url_s,geo";
    private static final int IMAGEVIEW_WIDTH = 150;
    private static final int IMAGEVIEW_HEIGHT = 150;

    private GoogleApiClient mGoogleClient;

    private GoogleMap mGoogleMap;

    private Location mCurrentLocation;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                getLocation();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getLocation() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            SnackBarUtils.showPlainSnackBar(getActivity(), R.string.snackbar_network_unavailable);
        } else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setNumUpdates(1);
            locationRequest.setInterval(0);


            if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                SnackBarUtils.showPlainSnackBar(getActivity(), R.string.snackbar_location_permissions_error);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = location;
                        Log.i(TAG, "Latitude: " + mCurrentLocation.getLatitude() + ", Longitude: " + mCurrentLocation.getLongitude());
                        getFlickrPhotos(mCurrentLocation);
                    }
                });
            }
        }
    }

    private void getFlickrPhotos(Location location) {
        final Dialog progressDialog = DialogUtils.showProgressDialog(getActivity());
        FlickrService flickrService = RetrofitSingleton.getInstance(URL).create(FlickrService.class);

        String searchString = "dog";

        flickrService.searchPhotos(FLICKR_API_SEARCH_PHOTOS, FLICKR_API_KEY, FLICKR_API_FORMAT, FLICKR_API_JSON_CALLBACK, searchString, location.getLatitude(), location.getLongitude(), FLICKR_API_EXTRAS).enqueue(new Callback<PhotosObject>() {
            @Override
            public void onResponse(Response<PhotosObject> response) {
                if (response.isSuccess()) {
                    mPhotoList = response.body().getPhotos().getPhoto();
                    updateUI();
                } else {
                    Log.e(TAG, "Error: " + response.message());
                    showImageError();
                }
                dismissDialog(progressDialog);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error: " + t.toString());
                dismissDialog(progressDialog);
                showImageError();
            }
        });
    }

    private void updateUI() {
        if (mPhotoList.isEmpty() || mPhotoList.get(0).getUrl() == null) {
            showImageError();
        } else {
            Log.d(TAG, "image latitude: " + mPhotoList.get(0).getLatitude());
            Log.d(TAG, "image longitude: " + mPhotoList.get(0).getLongitude());

            if (mGoogleMap != null) {
                Picasso.with(getActivity())
                    .load(Uri.parse(mPhotoList.get(0).getUrl()))
                    .resize(IMAGEVIEW_WIDTH, IMAGEVIEW_HEIGHT)
                    .centerCrop()
                    .into(mBitmapTarget);
            }
        }
    }

    private Target mBitmapTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(TAG, "bitmap successfully loaded");

            LatLng itemPoint = new LatLng(mPhotoList.get(0).getLatitude(), mPhotoList.get(0).getLongitude());
            LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(bitmap);
            MarkerOptions itemMarker = new MarkerOptions()
                    .position(itemPoint)
                    .icon(itemBitmap);
            MarkerOptions myMarker = new MarkerOptions()
                    .position(myPoint);

            mGoogleMap.clear();
            mGoogleMap.addMarker(itemMarker);
            mGoogleMap.addMarker(myMarker);

            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(itemPoint)
                    .include(myPoint)
                    .build();

            int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, margin);
            mGoogleMap.animateCamera(cameraUpdate);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d(TAG, "bitmap failed to load");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void showImageError() {
        SnackBarUtils.showPlainSnackBar(getActivity(), R.string.snackbar_image_download_error);
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

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
            }
        });
    }
}
