package com.example.test.db

import androidx.room.*

@Dao
interface ItemDAO {
    @Insert
    suspend fun insertData(keyword: ItemData)

    @Query("select * from status_item order by itemOrder ASC")
    suspend fun getAllItemData(): List<ItemData>

    @Query("delete from status_item")
    suspend fun deleteAllItemData()

    @Delete
    suspend fun deleteItemData(item: ItemData)

    @Update
    suspend fun updateItemData(item: ItemData)
}