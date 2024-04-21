package com.example.test.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDAO {
    @Insert
    fun insertData(keyword: ItemData)

    @Query("select * from status_item order by itemOrder ASC")
    fun getAllItemData(): LiveData<List<ItemData>>

    @Query("select count(itemName) from status_item where itemName=:item")
    fun itemContains(item: String): Long

    @Query("delete from status_item")
    fun deleteAllItemData()

    @Delete
    fun deleteItemData(item: ItemData)

    @Update
    fun updateItemData(item: ItemData)
}