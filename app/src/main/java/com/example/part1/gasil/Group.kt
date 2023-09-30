package com.example.part1.gasil

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group")
data class Group (
    val group : String,
    val color : String,
    @PrimaryKey(autoGenerate= true)val id: Int = 0,
)