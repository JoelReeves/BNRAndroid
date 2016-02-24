package com.bromancelabs.photogallery.activities

import com.bromancelabs.photogallery.fragments.PhotoGalleryFragmentKtKt

class PhotoGalleryActivityKt : SingleFragmentActivityKt() {

    override fun createFragment() = PhotoGalleryFragmentKtKt.newInstance()
}