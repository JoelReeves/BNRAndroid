package com.bromancelabs.photogallery.activities

import com.bromancelabs.photogallery.fragments.PhotoGalleryFragmentKt

class PhotoGalleryActivityKt : SingleFragmentActivityKt() {

    override fun createFragment() = PhotoGalleryFragmentKt.newInstance()
}