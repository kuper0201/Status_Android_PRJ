package com.example.test.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ItemData::class], version=2, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun itemDAO() : ItemDAO

    // Singleton 클래스 구현
    companion object{
        // DB 이름
        private const val dbName = "status_db"
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase?{
            if(INSTANCE == null){
                synchronized(LocalDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, dbName)
                        .fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }
    }
}