package com.bromancelabs.photogallery.fragments

import android.app.Activity
import android.app.Dialog
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bromancelabs.photogallery.R
import com.bromancelabs.photogallery.activities.PhotoGalleryActivity
import com.bromancelabs.photogallery.models.PhotoKt
import com.bromancelabs.photogallery.models.PhotosObjectKt
import com.bromancelabs.photogallery.services.*
import com.bromancelabs.photogallery.utils.NetworkUtils
import com.bromancelabs.photogallery.utils.SnackBarUtils
import com.bromancelabs.photogallery.utils.showPlainSnackBar
import com.bromancelabs.photogallery.utils.showProgressDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo_gallery.*
import kotlinx.android.synthetic.main.photo_item.view.*
import kotlinx.android.synthetic.main.fragment_photo_gallery.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotoGalleryFragmentKt : VisibleFragmentKt() {

    companion object {
        val TAG = PhotoGalleryFragmentKt::class.java.simpleName
        private val GRID_COLUMNS = 3
        private val IMAGEVIEW_WIDTH = 150
        private val IMAGEVIEW_HEIGHT = 150
        val POLL_INTENT = "poll_intent"
        val POLL_KEY_ID = "id"

        fun newInstance() = PhotoGalleryFragmentKt()
    }

    val recyclerView by lazy { rv_photo_gallery }
    val flickrService by lazy { FlickrServiceKt.getInstance() }
    var photoAdapter: PhotoAdapterKt? = null
    var progressDialog: Dialog? = null
    var lastResultId: String?
        get() = QueryPreferencesKt.getLastResultId(activity)
        set(value) {
            value?.let{ QueryPreferencesKt.setLastResultId(activity, value) }
        }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Query: ${QueryPreferences.getSearchQuery(activity)}")
            Log.d(TAG, "ID: ${intent.getStringExtra(POLL_KEY_ID)}")
            lastResultId = intent.getStringExtra(POLL_KEY_ID)
            getFlickrPhotos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.layoutManager = GridLayoutManager(activity, GRID_COLUMNS)
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver, IntentFilter(POLL_INTENT))

        if (!NetworkUtils.isNetworkAvailable(activity)) {
            SnackBarUtils.showPlainSnackBar(activity, R.string.snackbar_network_unavailable)
        } else {
            val isOn = QueryPreferences.isAlarmOn(activity)
            PollService.setServiceAlarm(activity, isOn)
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mMessageReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                if (!TextUtils.isEmpty(s)) {
                    QueryPreferences.setSearchQuery(activity, s)
                    hideKeyboard()
                    searchItem.collapseActionView()
                    getFlickrPhotos()
                }
                return true
            }

            override fun onQueryTextChange(s: String) = false
        })

        searchView.setOnSearchClickListener { searchView.setQuery(QueryPreferences.getSearchQuery(activity), false) }

        val toggleItem = menu.findItem(R.id.menu_item_toggle_polling)
        toggleItem.setTitle(if (PollService.isServiceAlarmOn(activity)) R.string.stop_polling else R.string.start_polling)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_clear -> {
                QueryPreferences.setSearchQuery(activity, null)
                getFlickrPhotos()
                return true
            }
            R.id.menu_item_toggle_polling -> {
                startPolling()
                activity.invalidateOptionsMenu()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun startPolling() {
        val shouldStartAlarm = !PollService.isServiceAlarmOn(activity)
        PollService.setServiceAlarm(activity, shouldStartAlarm)
    }

    private fun getFlickrPhotos() {
        cancelPhotosObjectRequests()

        progressDialog = showProgressDialog(activity)
        photoAdapter?.clearAdapter()

        val searchString: String? = QueryPreferences.getSearchQuery(activity)
        val callback: Callback<PhotosObjectKt> = populatePhotoListAdapter { it.photos.photo }

        if (null == searchString || TextUtils.isEmpty(searchString)) {
            flickrService.getRecentPhotos().enqueue(callback)
        } else {
            flickrService.searchPhotos(searchString).enqueue(callback)
        }
    }

    private inline fun <T> populatePhotoListAdapter(crossinline extract: (T) -> List<PhotoKt>) = retrofitCallback<T> { call, response ->
        if (response.isSuccess) {
            setupAdapter(extract(response.body()))
        } else {
            Toast.makeText(activity, "Failed to download photos", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Failed to download photos (response: ${response.code()}")
        }
    }

    private inline fun <T> retrofitCallback(crossinline code: (Call<T>, Response<T>) -> Unit): Callback<T> = object : Callback<T> {

        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            if (null == call) Log.e(TAG, "Network error receiving call result")
            if (null == response) Log.e(TAG, "Network error receiving response")

            if (null != call && null != response) {
                code(call, response)
            }

            progressDialog.dismiss()
        }

        override fun onFailure(call: Call<T>?, t: Throwable?) {
            Log.e(TAG, "Network Failure (Error): ${t?.message}", t)
            progressDialog.dismiss()
            showErrorSnackBar()
        }
    }

    private fun cancelPhotosObjectRequests() {
        progressDialog.dismiss()
        flickrService.getRecentPhotos().cancel()
        QueryPreferencesKt.getSearchQuery(activity)?.let { flickrService.searchPhotos(it).cancel() }
    }

    private fun setupAdapter(photoList: List<PhotoKt>) {
        if (isAdded && !photoList.isEmpty()) {
            setLastResultId(photoList)
            photoAdapter = PhotoAdapterKt(photoList)
            recyclerView.setAdapter(photoAdapter)
        } else {
            showErrorSnackBar()
        }
    }

    private fun setLastResultId(photoList: List<PhotoKt>) {
        val resultId = photoList[0].id

        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result: $resultId")
            showBackgroundNotification(0, createNotification())
            activity.sendBroadcast(Intent(PollService.ACTION_SHOW_NOTIFICATION), PollService.PRIVATE_PERMISSION)
        }

        lastResultId = resultId
    }

    private fun createNotification(): Notification {
        val intent = PhotoGalleryActivity.newIntent(activity)
        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)

        return NotificationCompat.Builder(activity)
                .setTicker(activity.resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(activity.resources.getString(R.string.new_pictures_title))
                .setContentText(activity.resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification) {
        val intent = Intent(PollService.ACTION_SHOW_NOTIFICATION)
        intent.putExtra(PollService.REQUEST_CODE, requestCode)
        intent.putExtra(PollService.NOTIFICATION, notification)
        activity.sendOrderedBroadcast(intent, PollService.PRIVATE_PERMISSION, null, null, Activity.RESULT_OK, null, null)
    }

    fun Dialog?.dismiss() = this?.dismiss()

    fun showErrorSnackBar() = showPlainSnackBar(activity, R.string.snackbar_download_error)

    fun hideKeyboard() {
        val inputMethodManager: InputMethodManager? = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(activity.currentFocus.windowToken, 0)
    }

    inner class PhotoAdapterKt(var photoList: List<PhotoKt>) : RecyclerView.Adapter<PhotoHolderKt>() {

        override fun getItemCount() = photoList.size

        override fun onBindViewHolder(holder: PhotoHolderKt, position: Int) = holder.bindPhoto(photoList[position])

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolderKt? {
            val view = LayoutInflater.from(activity).inflate(R.layout.photo_item, parent, false)
            return PhotoHolderKt(view)
        }

        fun clearAdapter() {
            val size = itemCount
            photoList = emptyList()
            notifyItemRangeRemoved(0, size)
        }
    }

    inner class PhotoHolderKt(view: View) : RecyclerView.ViewHolder(view) {

        fun bindPhoto(photo: PhotoKt) {
            Picasso.with(activity)
                .load(photo.url?.let { Uri.parse(it) })
                .placeholder(R.drawable.ic_placeholder_image)
                .error(R.drawable.ic_error_image)
                .resize(IMAGEVIEW_WIDTH, IMAGEVIEW_HEIGHT)
                .centerCrop()
                .into(itemView.iv_fragment_photo_gallery)
        }
    }
}