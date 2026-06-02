package com.example.musculation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seances")
data class SeanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titre: String,
    val date: String,
    val duree: String
)