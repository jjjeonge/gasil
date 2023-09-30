package com.example.part1.gasil

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GroupDao {
    @Query("SELECT * from `group` ORDER BY id DESC")
    fun getAll(): List<Group>

    @Query("SELECT * from `group` ORDER BY id DESC LIMIT 10")
    fun getLatestInfo() : Group

    @Query("SELECT `group` from `group`")
    fun getGroupName() : List<String>

    @Query("SELECT `color` from `group` WHERE `group` = :groupName")
    fun getGroupColor(groupName: String): String

    @Insert
    fun insert(info: Group)

    @Delete
    fun delete(info: Group)

    @Update
    fun update(info: Group)
}