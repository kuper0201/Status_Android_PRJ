package com.example.test.repo

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ImageRepo {
    val imageList: MutableLiveData<List<Bitmap>>

    constructor() {
        imageList = MutableLiveData(ArrayList())
    }

    fun getImages(): LiveData<List<Bitmap>> {
        return imageList
    }

    fun setImages(images: List<Bitmap>) {
        imageList.value = images
    }
}