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
import android.widget.TextView;

import com.bromancelabs.photogallery.R;
import com.bromancelabs.photogallery.models.Photo;
import com.bromancelabs.photogallery.models.PhotosObject;
import com.bromancelabs.photogallery.services.FlickerPhotoService;
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
    private static final int SCROLL_DOWN = 1;
    private static final int SCROLL_UP = -1;
    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private static final String URL = "https://api.flickr.com/services/rest/";
    private static final String FLICKER_API_KEY = "b71c3d2d57d035bf593c78dcb4b659d1";
    private static final String FLICKER_API_METHOD = "flickr.photos.getRecent";
    private static final String FLICKER_API_FORMAT = "json";
    private static final String FLICKER_API_JSON_CALLBACK = "1";
    private static final String FLICKER_API_EXTRAS = "url_s";

    @Bind(R.id.rv_photo_gallery) RecyclerView mPhotoRecyclerView;

    private List<Photo> mPhotoList;

    private int mCurrentPage = 1;
    private int mStartingPage;
    private int mEndingPage;

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
            retroFitRequest(mCurrentPage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void retroFitRequest(int currentPage) {
        final Dialog dialog = DialogUtils.showProgressDialog(getActivity());

        mCurrentPage = currentPage;

        FlickerPhotoService mFlickerPhotoService = RetrofitSingleton.getInstance(URL).create(FlickerPhotoService.class);

        mFlickerPhotoService.getRecentPhotos(FLICKER_API_METHOD, FLICKER_API_KEY, FLICKER_API_FORMAT, FLICKER_API_JSON_CALLBACK, FLICKER_API_EXTRAS, mCurrentPage).enqueue(new Callback<PhotosObject>() {
            @Override
            public void onResponse(Response<PhotosObject> response) {
                if (response.isSuccess()) {
                    //https://www.flickr.com/services/api/explore/flickr.photos.getRecent
                    final int responseSize = Integer.parseInt(response.body().getPhotos().getTotal());
                    Log.d(TAG, "JSON response # of photos: " + responseSize);

                    Log.d(TAG, "current page: " + mCurrentPage);

                    mStartingPage = responseSize / (response.body().getPhotos().getPerpage() * response.body().getPhotos().getPages());
                    Log.d(TAG, "starting page: " + mStartingPage);

                    mEndingPage = responseSize / response.body().getPhotos().getPages();
                    Log.d(TAG, "ending page: " + mEndingPage);

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

            mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy < 0 && mCurrentPage > mStartingPage) {
                        if (!mPhotoRecyclerView.canScrollVertically(SCROLL_UP)) {
                            mCurrentPage--;
                            retroFitRequest(mCurrentPage);
                        }
                    }

                    if (dy > 0 && mCurrentPage < mEndingPage) {
                        if (!mPhotoRecyclerView.canScrollVertically(SCROLL_DOWN)) {
                            mCurrentPage++;
                            retroFitRequest(mCurrentPage);
                        }
                    }
                }
            });
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
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
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
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }

        public void bindPhoto(Photo photo) {
            mTitleTextView.setText(photo.toString());
        }
    }
}
