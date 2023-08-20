package com.example.part1.gasil

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AccountInfo::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun accountInfoDao(): AccountInfoDao

    companion object {
        private var INSTANCE: AppDataBase? = null
        fun getInstance(context: Context): AppDataBase? {
            if(INSTANCE == null) {
                synchronized(AppDataBase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "app-database.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}