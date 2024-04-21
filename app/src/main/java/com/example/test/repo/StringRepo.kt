package com.example.test.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class StringRepo {
    val strMap: MutableLiveData<HashMap<String, String>>

    constructor() {
        strMap = MutableLiveData(HashMap())
    }

    fun getStrMap(): LiveData<HashMap<String, String>> {
        return strMap
    }

    fun updateStr(key: String, to: String) {
        val t = strMap.value!!
        t.put(key, to)
        strMap.value = t
    }
}