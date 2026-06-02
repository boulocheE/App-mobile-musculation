package com.example.musculation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musculation.data.SeanceComplete


data class Performance(
    val nomExo: String,
    val date: String,
    val poids: String,
    val reps: String,
    val intensite: Double
)

@Composable
fun HomeScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.chargerDonnees(context)
    }

    val listeSeances by viewModel.historiqueSeances.collectAsState()
    val scrollState = rememberScrollState()

    var seanceSelectionnee by remember { mutableStateOf<SeanceComplete?>(null) }

    BackHandler(enabled = seanceSelectionnee != null) {
        seanceSelectionnee = null
    }

    val dernieresSeances = listeSeances.take(2)

    val topSeries = remember(listeSeances) {
        listeSeances.flatMap { seanceComplete ->
            seanceComplete.exercices.flatMap { exerciceAvecSeries ->
                exerciceAvecSeries.series.map { serie ->
                    val safePoids = serie.poids.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val safeReps = serie.reps.toDoubleOrNull() ?: 0.0

                    val scoreIntensite = safePoids * safeReps

                    Performance(
                        nomExo = exerciceAvecSeries.exercice.nom,
                        date = seanceComplete.seance.date,
                        poids = serie.poids,
                        reps = serie.reps,
                        intensite = scoreIntensite
                    )
                }
            }
        }
            .sortedByDescending { it.intensite }
            .take(2) // les 2 meilleurs
    }

    if (seanceSelectionnee == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Column {
                Text(
                    text = "TABLEAU DE BORD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Musculation",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (listeSeances.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aucune activité pour le moment.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f).height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Row {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(modifier = Modifier.fillMaxWidth(), text = "${listeSeances.size}", fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                            }
                            Text(text = "Séances totales", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f).height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Row {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Text(modifier = Modifier.fillMaxWidth(), text = dernieresSeances.firstOrNull()?.seance?.duree ?: "--", fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                            }
                            Text(text = "Dernière durée", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (topSeries.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Performances maximales", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        }

                        topSeries.forEach { perf ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = perf.nomExo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(text = perf.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Box(
                                        modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "${perf.poids} kg x ${perf.reps}",
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Derniers entraînements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    dernieresSeances.forEach { seance ->
                        ItemSeanceHistoriquePro(
                            seanceComplete = seance,
                            codeClick = {
                                seanceSelectionnee = seance
                            }
                        )
                    }
                }
            }
        }
    } else {
        SeanceDetailView(
            seanceComplete = seanceSelectionnee!!,
            onBackClick = { seanceSelectionnee = null },
            onDeleteClick = {
                viewModel.supprimerSeanceComplete(context, seanceSelectionnee!!)
                seanceSelectionnee = null
            },
            onSaveClick = { seanceTotalementModifiee ->
                // Sauvegarde dans SQLite via le ViewModel
                viewModel.sauvegarderSeanceCompleteModifiee(context, seanceTotalementModifiee)

                // Maj l'aperçu instantanément à l'écran
                seanceSelectionnee = seanceTotalementModifiee
            }
        )
    }
}