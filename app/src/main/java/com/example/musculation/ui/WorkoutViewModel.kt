package com.example.musculation.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musculation.data.ExerciceEntity
import com.example.musculation.data.MusculationDatabase
import com.example.musculation.data.SeanceEntity
import com.example.musculation.data.SerieEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutViewModel : ViewModel() {

    private var _seanceDao: com.example.musculation.data.SeanceDAO? = null

    fun initDatabase(context: Context) {
        if (_seanceDao == null) {
            _seanceDao = MusculationDatabase.getDatabase(context).seanceDAO()
        }
    }

    var isWorkoutActive by mutableStateOf(false)
        private set

    var titreSeance by mutableStateOf("Ma Séance")

    var exercicesEnCours = mutableStateListOf<Pair<String, List<Pair<String, String>>>>()
        private set

    private var startTimeMillis: Long = 0L

    fun lancerNouvelleSeance(titreChoisi: String) {
        titreSeance = titreChoisi.ifBlank { "Nouvelle Séance" }
        startTimeMillis = System.currentTimeMillis()
        exercicesEnCours.clear()
        isWorkoutActive = true
    }

    fun ajouterExerciceAMaSeance(nomExo: String, series: List<Pair<String, String>>) {
        if (nomExo.isNotBlank() && series.isNotEmpty()) {
            exercicesEnCours.add(Pair(nomExo, series))
        }
    }

    fun enregistrerSeance() {
        val dao = _seanceDao ?: return

        val endTimeMillis = System.currentTimeMillis()
        val diffMinutes = (endTimeMillis - startTimeMillis) / (1000 * 60)

        val heures = diffMinutes / 60
        val minutesRestantes = diffMinutes % 60

        val dureeCalculee = when {
            heures > 0 -> "${heures}h ${minutesRestantes}m"
            diffMinutes > 0 -> "$diffMinutes min"
            else -> "< 1 min"
        }

        viewModelScope.launch {
            val dateDuJour = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()).uppercase()

            val seanceId = dao.insertSeance(
                SeanceEntity(titre = titreSeance, date = dateDuJour, duree = dureeCalculee)
            )

            exercicesEnCours.forEach { (nomExo, listeSeries) ->
                val exerciceId = dao.insertExercice(
                    ExerciceEntity(seanceId = seanceId, nom = nomExo)
                )

                val seriesEntities = listeSeries.mapIndexed { index, (poids, reps) ->
                    SerieEntity(
                        exerciceId = exerciceId,
                        numero = index + 1,
                        poids = poids,
                        reps = reps
                    )
                }
                dao.insertSeries(seriesEntities)
            }

            isWorkoutActive = false
            exercicesEnCours.clear()
        }
    }

    fun annulerSeance() {
        isWorkoutActive = false
        exercicesEnCours.clear()
    }
}