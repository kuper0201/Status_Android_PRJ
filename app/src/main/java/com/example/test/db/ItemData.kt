package com.example.test.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_item")
data class ItemData(
    @PrimaryKey
    var itemName: String,
    var itemType: Boolean,
    var itemOrder: Int
    //var createdDateTime: ZonedDateTime = ZonedDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        val o = other as ItemData
        return this.itemName.equals(o.itemName)
    }

    override fun hashCode(): Int {
        return itemName.hashCode()
    }
}