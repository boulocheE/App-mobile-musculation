package com.example.musculation.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musculation.data.MusculationDatabase
import com.example.musculation.data.SeanceComplete
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val _historiqueSeances = MutableStateFlow<List<SeanceComplete>>(emptyList())
    val historiqueSeances: StateFlow<List<SeanceComplete>> = _historiqueSeances.asStateFlow()

    fun chargerDonnees(context: Context) {
        val seanceDao = MusculationDatabase.getDatabase(context).seanceDAO()
        viewModelScope.launch {
            seanceDao.getAllSeancesCompletes().collect { liste ->
                _historiqueSeances.value = liste
            }
        }
    }

    fun supprimerSeanceComplete(context: Context, seanceComplete: SeanceComplete) {
        viewModelScope.launch {
            val dao = MusculationDatabase.getDatabase(context).seanceDAO()
            seanceComplete.exercices.forEach { exo ->
                dao.deleteSeries(exo.series)
                dao.deleteExercice(exo.exercice)
            }
            dao.deleteSeance(seanceComplete.seance)
            chargerDonnees(context)
        }
    }

    fun sauvegarderSeanceCompleteModifiee(context: Context, seanceModifiee: SeanceComplete) {
        viewModelScope.launch {
            val dao = MusculationDatabase.getDatabase(context).seanceDAO()

            dao.updateSeance(seanceModifiee.seance)

            seanceModifiee.exercices.forEach { exoAvecSeries ->
                dao.updateExercice(exoAvecSeries.exercice)

                exoAvecSeries.series.forEach { serie ->
                    dao.updateSerie(serie)
                }
            }

            chargerDonnees(context)
        }
    }
}