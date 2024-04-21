package com.example.test.repo

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.test.db.ItemDAO
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class DataRepo(application: Application) {
    private val itemDAO: ItemDAO

    init {
        var db : LocalDatabase = LocalDatabase.getInstance(application)!!
        itemDAO = db.itemDAO()
    }

    fun getItemList() : LiveData<List<ItemData>> {
        return itemDAO.getAllItemData()
    }

    suspend fun insertData(item: ItemData): Boolean {
        var count: Long = 0
        val ret = CoroutineScope(Dispatchers.IO).async {
            count = itemDAO.itemContains(item.itemName)
        }

        ret.await()
        if(count == 0L) {
            CoroutineScope(Dispatchers.IO).launch {
                itemDAO.insertData(item)
            }
            return true
        }
        return false
    }

    fun deleteItemData(item: ItemData) {
        CoroutineScope(Dispatchers.IO).launch {
            itemDAO.deleteItemData(item)
        }
    }

    fun reorderItem(itemList: List<ItemData>) {
        CoroutineScope(Dispatchers.IO).launch {
            itemDAO.deleteAllItemData()
            for ((idx, item) in itemList.withIndex()) {
                itemDAO.insertData(ItemData(itemName = item.itemName, itemType = item.itemType, itemOrder = idx))
            }
        }
    }


}