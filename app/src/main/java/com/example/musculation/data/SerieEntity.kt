package com.example.musculation.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "series",
    foreignKeys = [
        ForeignKey(
            entity = ExerciceEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SerieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciceId: Long,
    val numero: Int,
    val poids: String,
    val reps: String
)