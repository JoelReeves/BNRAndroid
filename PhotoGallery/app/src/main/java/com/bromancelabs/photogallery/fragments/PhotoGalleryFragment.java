package com.bromancelabs.photogallery.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bromancelabs.photogallery.R;
import com.bromancelabs.photogallery.models.Photo;
import com.bromancelabs.photogallery.models.PhotosObject;
import com.bromancelabs.photogallery.services.FlickrPhotoService;
import com.bromancelabs.photogallery.services.RetrofitSingleton;
import com.bromancelabs.photogallery.utils.DialogUtils;
import com.bromancelabs.photogallery.utils.NetworkUtils;
import com.bromancelabs.photogallery.utils.SnackBarUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoGalleryFragment extends Fragment {
    private static final int GRID_COLUMNS = 3;
    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private static final String URL = "https://api.flickr.com/services/rest/";
    private static final String FLICKR_API_KEY = "b71c3d2d57d035bf593c78dcb4b659d1";
    private static final String FLICKR_API_METHOD = "flickr.photos.getRecent";
    private static final String FLICKR_API_FORMAT = "json";
    private static final String FLICKR_API_JSON_CALLBACK = "1";
    private static final String FLICKR_API_EXTRAS = "url_s";

    @Bind(R.id.rv_photo_gallery) RecyclerView mPhotoRecyclerView;

    private List<Photo> mPhotoList;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID_COLUMNS));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            SnackBarUtils.showPlainSnackBar(getActivity(), R.string.snackbar_network_unavailable);
        } else {
            retroFitRequest();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void retroFitRequest() {
        final Dialog dialog = DialogUtils.showProgressDialog(getActivity());

        FlickrPhotoService mFlickrPhotoService = RetrofitSingleton.getInstance(URL).create(FlickrPhotoService.class);

        mFlickrPhotoService.getRecentPhotos(FLICKR_API_METHOD, FLICKR_API_KEY, FLICKR_API_FORMAT, FLICKR_API_JSON_CALLBACK, FLICKR_API_EXTRAS).enqueue(new Callback<PhotosObject>() {
            @Override
            public void onResponse(Response<PhotosObject> response) {
                if (response.isSuccess()) {
                    final long responseSize = Long.parseLong(response.body().getPhotos().getTotal());
                    Log.d(TAG, "JSON response # of photos: " + responseSize);

                    dismissDialog(dialog);

                    mPhotoList = response.body().getPhotos().getPhoto();
                    setupAdapter();

                } else {
                    Log.e(TAG, "Error: " + response.message());
                    dismissDialog(dialog);
                    showErrorSnackBar();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error: " + t.toString());
                dismissDialog(dialog);
                showErrorSnackBar();
            }
        });
    }

    private void setupAdapter() {
        if (isAdded() && !mPhotoList.isEmpty()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mPhotoList));
        } else {
            showErrorSnackBar();
        }
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void showErrorSnackBar() {
        SnackBarUtils.showPlainSnackBar(getActivity(), R.string.snackbar_download_error);
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<Photo> mPhotoList;

        public PhotoAdapter(List<Photo> photoList) {
            mPhotoList = photoList;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.photo_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            Photo photo = mPhotoList.get(position);
            holder.bindPhoto(photo);
        }

        @Override
        public int getItemCount() {
            return mPhotoList.size();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_fragment_photo_gallery) ImageView mPhotoImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindPhoto(Photo photo) {

        }
    }
}
