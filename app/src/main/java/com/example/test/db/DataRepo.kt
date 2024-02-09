package com.example.test.db

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DataRepo {
    private var _imgList: MutableLiveData<ArrayList<Bitmap>>
    private var _itemList: MutableLiveData<ArrayList<ItemData>>
    private var _stringList: MutableLiveData<HashMap<String, String>>

    constructor() {
        _imgList = MutableLiveData(ArrayList())
        _itemList = MutableLiveData(ArrayList())
        _stringList = MutableLiveData(HashMap())
    }

    fun getImgList() : MutableLiveData<ArrayList<Bitmap>> {
        return _imgList
    }

    fun getItemList() : MutableLiveData<ArrayList<ItemData>> {
        return _itemList
    }

    fun getStringList() : MutableLiveData<HashMap<String, String>> {
        return _stringList
    }
}