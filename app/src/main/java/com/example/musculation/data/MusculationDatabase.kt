package com.example.musculation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SeanceEntity::class, ExerciceEntity::class, SerieEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusculationDatabase : RoomDatabase() {

    abstract fun seanceDAO(): SeanceDAO

    companion object {
        @Volatile
        private var INSTANCE: MusculationDatabase? = null

        fun getDatabase(context: Context): MusculationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusculationDatabase::class.java,
                    "musculation_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}