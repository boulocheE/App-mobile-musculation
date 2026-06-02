package com.example.musculation.data

import androidx.room.Embedded
import androidx.room.Relation

data class ExerciceAvecSeries(
    @Embedded val exercice: ExerciceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciceId"
    )
    val series: List<SerieEntity>
)

data class SeanceComplete(
    @Embedded val seance: SeanceEntity,
    @Relation(
        entity = ExerciceEntity::class,
        parentColumn = "id",
        entityColumn = "seanceId"
    )
    val exercices: List<ExerciceAvecSeries>
)