package com.example.musculation.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercices",
    foreignKeys = [
        ForeignKey(
            entity = SeanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["seanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val seanceId: Long,
    val nom: String
)