package com.bromancelabs.photogallery.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bromancelabs.photogallery.R;
import com.bromancelabs.photogallery.activities.PhotoGalleryActivity;
import com.bromancelabs.photogallery.activities.PhotoPageActivity;
import com.bromancelabs.photogallery.models.Photo;
import com.bromancelabs.photogallery.models.PhotosObject;
import com.bromancelabs.photogallery.services.FlickrService;
import com.bromancelabs.photogallery.services.PollService;
import com.bromancelabs.photogallery.services.QueryPreferences;
import com.bromancelabs.photogallery.services.RetrofitSingleton;
import com.bromancelabs.photogallery.utils.DialogUtils;
import com.bromancelabs.photogallery.utils.NetworkUtils;
import com.bromancelabs.photogallery.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoGalleryFragment extends VisibleFragment {
    private static final int GRID_COLUMNS = 3;
    private static final int IMAGEVIEW_WIDTH = 150;
    private static final int IMAGEVIEW_HEIGHT= 150;
    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private static final String URL = "https://api.flickr.com/services/rest/";
    private static final String FLICKR_API_KEY = "b71c3d2d57d035bf593c78dcb4b659d1";
    private static final String FLICKR_API_GET_RECENT_PHOTOS = "flickr.photos.getRecent";
    private static final String FLICKR_API_SEARCH_PHOTOS = "flickr.photos.search";
    private static final String FLICKR_API_FORMAT = "json";
    private static final String FLICKR_API_JSON_CALLBACK = "1";
    private static final String FLICKR_API_EXTRAS = "url_s";
    public static final String POLL_INTENT = "poll_intent";
    public static final String POLL_KEY_ID = "id";

    @Bind(R.id.rv_photo_gallery) RecyclerView mPhotoRecyclerView;

    private PhotoAdapter mPhotoAdapter;

    private FlickrService mFlickrService;

    private Dialog mProgressDialog;

    private String mLastResultId;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
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
        PollService.setServiceAlarm(getActivity(), false);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(POLL_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s)) {
                    QueryPreferences.setSearchQuery(getActivity(), s);
                    hideKeyboard();
                    searchItem.collapseActionView();
                    getFlickrPhotos();
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(QueryPreferences.getSearchQuery(getActivity()), false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setSearchQuery(getActivity(), null);
                getFlickrPhotos();
                return true;
            case R.id.menu_item_toggle_polling:
                startPolling();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Query: " + QueryPreferences.getSearchQuery(getActivity()));
            Log.d(TAG, "ID: " + intent.getStringExtra(POLL_KEY_ID));
            mLastResultId = intent.getStringExtra(POLL_KEY_ID);
            getFlickrPhotos();
        }
    };

    private void startPolling() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            SnackBarUtils.showPlainSnackBar(getActivity(), R.string.snackbar_network_unavailable);
        } else {
            boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
            PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
        }
    }

    private void getFlickrPhotos() {
        cancelPhotosObjectRequests();

        if (mPhotoAdapter != null) {
            mPhotoAdapter.clearAdapter();
        }

        mProgressDialog = DialogUtils.showProgressDialog(getActivity());
        mFlickrService = RetrofitSingleton.getInstance(URL).create(FlickrService.class);

        String searchString = QueryPreferences.getSearchQuery(getActivity());

        if (TextUtils.isEmpty(searchString)) {
            mFlickrService.getRecentPhotos(FLICKR_API_GET_RECENT_PHOTOS, FLICKR_API_KEY, FLICKR_API_FORMAT, FLICKR_API_JSON_CALLBACK, FLICKR_API_EXTRAS).enqueue(mPhotosObjectCallback);
        } else {
            mFlickrService.searchPhotos(FLICKR_API_SEARCH_PHOTOS, FLICKR_API_KEY, FLICKR_API_FORMAT, FLICKR_API_JSON_CALLBACK, searchString, FLICKR_API_EXTRAS).enqueue(mPhotosObjectCallback);
        }
    }

    private Callback<PhotosObject> mPhotosObjectCallback = new Callback<PhotosObject>() {
        @Override
        public void onResponse(Response<PhotosObject> response) {
            if (response.isSuccess()) {
                final long responseSize = Long.parseLong(response.body().getPhotos().getTotal());
                Log.d(TAG, "JSON response # of photos: " + responseSize);
                List<Photo> jsonResponce = response.body().getPhotos().getPhoto();
                setupAdapter(jsonResponce);
            } else {
                Log.e(TAG, "Error: " + response.message());
                showErrorSnackBar();
            }
            dismissDialog(mProgressDialog);
        }

        @Override
        public void onFailure(Throwable t) {
            Log.e(TAG, "Error: " + t.toString());
            dismissDialog(mProgressDialog);
            showErrorSnackBar();
        }
    };

    private void cancelPhotosObjectRequests() {
        dismissDialog(mProgressDialog);

        if (mFlickrService != null) {
            mFlickrService.getRecentPhotos(FLICKR_API_GET_RECENT_PHOTOS, FLICKR_API_KEY, FLICKR_API_FORMAT, FLICKR_API_JSON_CALLBACK, FLICKR_API_EXTRAS).cancel();
            mFlickrService.searchPhotos(FLICKR_API_SEARCH_PHOTOS, FLICKR_API_KEY, FLICKR_API_FORMAT, FLICKR_API_JSON_CALLBACK, QueryPreferences.getSearchQuery(getActivity()), FLICKR_API_EXTRAS).cancel();
        }
    }

    private void setupAdapter(List<Photo> photoList) {
        if (isAdded() && !photoList.isEmpty()) {
            setLastResultId(photoList);
            mPhotoAdapter = new PhotoAdapter(photoList);
            mPhotoRecyclerView.setAdapter(mPhotoAdapter);
        } else {
            showErrorSnackBar();
        }
    }

    private void setLastResultId(List<Photo> photoList) {
        String resultId = photoList.get(0).getId();

        if (resultId.equals(mLastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
            showBackgroundNotification(0, createNotification());
            getActivity().sendBroadcast(new Intent(PollService.ACTION_SHOW_NOTIFICATION), PollService.PRIVATE_PERMISSION);
        }

        QueryPreferences.setLastResultId(getActivity(), resultId);
    }

    private Notification createNotification() {
        Intent intent = PhotoGalleryActivity.newIntent(getActivity());
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        return new NotificationCompat.Builder(getActivity())
                .setTicker(getActivity().getResources().getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(getActivity().getResources().getString(R.string.new_pictures_title))
                .setContentText(getActivity().getResources().getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent intent = new Intent(PollService.ACTION_SHOW_NOTIFICATION);
        intent.putExtra(PollService.REQUEST_CODE, requestCode);
        intent.putExtra(PollService.NOTIFICATION, notification);
        getActivity().sendOrderedBroadcast(intent, PollService.PRIVATE_PERMISSION, null, null, Activity.RESULT_OK, null, null);
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void showErrorSnackBar() {
        SnackBarUtils.showPlainSnackBar(getActivity(), R.string.snackbar_download_error);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = getActivity().getCurrentFocus();

        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

        public void clearAdapter() {
            final int size = getItemCount();
            mPhotoList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_fragment_photo_gallery) ImageView mPhotoImageView;
        private Photo mPhoto;

        public PhotoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindPhoto(Photo photo) {
            mPhoto = photo;

            Picasso.with(getActivity())
                    .load(photo.getPhotoUri())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_error_image)
                    .resize(IMAGEVIEW_WIDTH, IMAGEVIEW_HEIGHT)
                    .centerCrop()
                    .into(mPhotoImageView);
        }

        @OnClick(R.id.iv_fragment_photo_gallery)
        public void flickrPhotoClicked() {
            startActivity(PhotoPageActivity.newIntent(getActivity(), mPhoto.getPhotoPageUri()));
        }
    }
}
