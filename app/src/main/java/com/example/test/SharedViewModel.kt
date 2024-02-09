package com.example.test

import android.content.ClipData.Item
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.test.db.DataRepo
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import kotlinx.coroutines.*
import java.util.Collections

class SharedViewModel : ViewModel {
    private val db: LocalDatabase
    val repo = DataRepo()

    constructor(context: Context) {
        this.db = LocalDatabase.getInstance(context)!!
    }

    fun insertMultiData(itemData: ArrayList<ItemData>) {
        val tmp = repo.getItemList().value!!
        val map = repo.getStringList().value!!
        val res = ArrayList<ItemData>()
        res.addAll(tmp)
        for(it in itemData) {
            res.add(it)
            map.put(it.itemName, "")
        }

        repo.getItemList().value = res
        CoroutineScope(Dispatchers.Main).launch {
            val ret = CoroutineScope(Dispatchers.IO).async {
                for(it in itemData) {
                    db.itemDAO().insertData(it)
                }
            }
        }
    }

    // 아이템 데이터 저장
    fun insertData(itemData: ItemData) {
        val tmp = repo.getItemList().value!!
        val res = ArrayList<ItemData>()
        res.addAll(tmp)
        res.add(itemData)
        repo.getItemList().value = res
        CoroutineScope(Dispatchers.Main).launch {
            val ret = CoroutineScope(Dispatchers.IO).async {
                db.itemDAO().insertData(itemData)
            }
        }

        val map = repo.getStringList().value!!
        map.put(itemData.itemName, "")
    }

    fun removeItem(pos: Int) {
        val itList = repo.getItemList().value!!
        val stringList = repo.getStringList().value!!

        val item = itList.get(pos)
        itList.remove(item)
        repo.getItemList().value = itList

        stringList.remove(item.itemName)

        CoroutineScope(Dispatchers.IO).launch {
            db.itemDAO().deleteAllItemData()
            for((idx, item) in itList.withIndex()) {
                var reorderedItem = ItemData(itemName = item.itemName, itemType = item.itemType, itemOrder = idx)
                db.itemDAO().insertData(reorderedItem)
            }
        }
    }

    fun reorderItem(itemList: ArrayList<ItemData>) {
        repo.getItemList().value = itemList
        CoroutineScope(Dispatchers.IO).launch {
            db.itemDAO().deleteAllItemData()
            for ((idx, item) in itemList.withIndex()) {
                db.itemDAO().insertData(ItemData(itemName = item.itemName, itemType = item.itemType, itemOrder = idx))
            }
        }
    }

    // 이미지 데이터 저장
    fun updateImageList(imgList: ArrayList<Bitmap>) {
        repo.getImgList().value = imgList
    }

    fun removeImg(pos: Int) {
        val tmp = repo.getImgList().value!!
        tmp.removeAt(pos)
        repo.getImgList().value = tmp
    }

    // 내용 데이터 저장
    fun updateStringData(key: String, value: String) {
        val tmp = repo.getStringList().value!!
        if(tmp.get(key) == null) {
            tmp.put(key, value)
        } else {
            tmp.set(key, value)
        }
        repo.getStringList().value = tmp
    }

    // 데이터 DB에서 가져오기
    fun getAllData() {
        CoroutineScope(Dispatchers.Main).launch {
            var data: ArrayList<ItemData> = ArrayList<ItemData>()
            val ret = CoroutineScope(Dispatchers.IO).async {
                data.addAll(db.itemDAO().getAllItemData())
            }

            ret.await()
            repo.getItemList().value = ArrayList(data)

            val map = HashMap<String, String>()
            for(d in data) {
                map.put(d.itemName, "")
            }

            repo.getStringList().value = map
        }
    }
}