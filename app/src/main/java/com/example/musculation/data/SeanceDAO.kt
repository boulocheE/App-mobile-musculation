package com.example.musculation.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SeanceDAO {

    @Transaction
    @Query("SELECT * FROM seances ORDER BY id DESC")
    fun getAllSeancesCompletes(): Flow<List<SeanceComplete>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeance(seance: SeanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercice(exercice: ExerciceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: List<SerieEntity>)

    @Query("DELETE FROM seances WHERE id = :seanceId")
    suspend fun deleteSeanceById(seanceId: Long)

    @Update
    suspend fun updateSeance(seance: SeanceEntity)

    @Update
    suspend fun updateExercice(exercice: ExerciceEntity)

    @Update
    suspend fun updateSerie(serie: SerieEntity)

    @Delete
    suspend fun deleteSeance(seance: SeanceEntity)

    @Delete
    suspend fun deleteExercice(exercice: ExerciceEntity)

    @Delete
    suspend fun deleteSeries(series: List<SerieEntity>)
}