package com.bromancelabs.photogallery.activities

import com.bromancelabs.photogallery.fragments.PhotoGalleryFragment

class PhotoGalleryActivityKt : SingleFragmentActivityKt() {

    override fun createFragment() = PhotoGalleryFragment.newInstance()
}