package com.example.test.item

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

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = DataRepo(application)

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ItemViewModel(application) as T
        }
    }

    fun getItemList(): LiveData<List<ItemData>> {
        return itemRepo.getItemList()
    }

    suspend fun insertData(item: ItemData) : Boolean {
        return itemRepo.insertData(item)
    }

    fun deleteItemData(item: ItemData) {
        itemRepo.deleteItemData(item)
    }

    fun reorderItem(itemList: List<ItemData>) {
        itemRepo.reorderItem(itemList)
    }
}