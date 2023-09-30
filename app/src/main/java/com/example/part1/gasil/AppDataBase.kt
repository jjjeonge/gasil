package com.example.part1.gasil

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Group::class],
    version = 3,
    exportSchema = true,
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    companion object {
        private var INSTANCE: AppDataBase? = null
        fun getInstance(context: Context): AppDataBase? {
            if(INSTANCE == null) {
                synchronized(AppDataBase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "app-database.db"
                    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
                }
            }
            return INSTANCE
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE 'group' ('color' TEXT NOT NULL, 'group' TEXT NOT NULL, 'id' INT NOT NULL, PRIMARY KEY('id'))")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE 'accountInfo'")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'group' CHANGE 'group' 'group_name' TEXT")
            }
        }

    }

}