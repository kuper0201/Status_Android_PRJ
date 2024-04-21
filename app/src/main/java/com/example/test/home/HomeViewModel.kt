package com.example.test.home

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.test.repo.DataRepo
import com.example.test.db.ItemData
import com.example.test.repo.ImageRepo
import com.example.test.repo.StringRepo

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = DataRepo(application)
    private val strRepo = StringRepo()
    private val imgRepo = ImageRepo()

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(application) as T
        }
    }

    fun getItemList(): LiveData<List<ItemData>> {
        return itemRepo.getItemList()
    }

    fun getStringMap(): LiveData<HashMap<String, String>> {
        return strRepo.getStrMap()
    }

    fun updateStr(origin: String, to: String) {
        strRepo.updateStr(origin, to)
    }

    fun getImages(): LiveData<List<Bitmap>> {
        return imgRepo.getImages()
    }
    fun setImages(images: List<Bitmap>) {
        imgRepo.setImages(images)
    }
}