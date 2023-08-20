package com.example.part1.gasil

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountInfoDao {
    @Query("SELECT * from accountInfo ORDER BY id DESC")
    fun getAll(): List<AccountInfo>

    @Query("SELECT * from accountInfo ORDER BY id DESC LIMIT 1")
    fun getLatestInfo() : AccountInfo

    @Insert
    fun insert(info: AccountInfo)

    @Delete
    fun delete(info: AccountInfo)

    @Update
    fun update(info: AccountInfo)
}