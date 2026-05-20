package com.example.musculation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SeanceHistorique(
    val id: Int,
    val titre: String,
    val date: String,
    val duree: String,
    val exercices: List<Exercice>
)

@Composable
fun HistoryScreen() {
    val scrollState = rememberScrollState()

    val fausseBaseDeDonnees = remember {
        listOf(
            SeanceHistorique(
                id = 1,
                titre = "Push Day • Pecs / Épaules / Triceps",
                date = "HIER",
                duree = "55 min",
                exercices = listOf(
                    Exercice("Développé Couché", listOf(Serie(1, "80", "10"), Serie(2, "85", "8"), Serie(3, "85", "8"))),
                    Exercice("Dips", listOf(Serie(1, "0", "12"), Serie(2, "0", "10")))
                )
            ),
            SeanceHistorique(
                id = 2,
                titre = "Legs Day • Squat Focus",
                date = "17 MAI",
                duree = "1h05",
                exercices = listOf(
                    Exercice("Squat Arrière", listOf(Serie(1, "100", "8"), Serie(2, "110", "6"), Serie(3, "110", "6"))),
                    Exercice("Presse à cuisses", listOf(Serie(1, "200", "12"), Serie(2, "220", "10")))
                )
            ),
            SeanceHistorique(
                id = 3,
                titre = "Pull Day • Dos / Biceps",
                date = "12 MAI",
                duree = "58 min",
                exercices = listOf(
                    Exercice("Tractions", listOf(Serie(1, "0", "8"), Serie(2, "0", "8"))),
                    Exercice("Tirage Bûcheron", listOf(Serie(1, "30", "10"), Serie(2, "32", "10")))
                )
            )
        )
    }

    var seanceSelectionnee by remember { mutableStateOf<SeanceHistorique?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (seanceSelectionnee == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ANCIENS ENTRAÎNÎEMENTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Historique",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.EventNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(fausseBaseDeDonnees) { seance ->
                    ItemSeanceHistoriquePro(seance = seance, codeClick = {
                        seanceSelectionnee = seance
                    })
                }
            }
        } else {
            val seance = seanceSelectionnee!!

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { seanceSelectionnee = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = "Détails de l'activité",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }


            // TODO icones
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column {
                    Text(
                        text = seance.titre,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${seance.date}  •  ${seance.duree}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                seance.exercices.forEach { exo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = exo.nom,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "SÉRIE", modifier = Modifier.width(50.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "KG", modifier = Modifier.width(54.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                Text(text = "REPS", modifier = Modifier.width(54.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }

                            exo.series.forEach { serie ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "${serie.numero}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    BadgeInfoHistorique(text = serie.poids)

                                    BadgeInfoHistorique(text = serie.reps)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ItemSeanceHistoriquePro(seance: SeanceHistorique, codeClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { codeClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = seance.titre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = seance.date, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = seance.duree,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


@Composable
fun BadgeInfoHistorique(text: String) {
    Box(
        modifier = Modifier
            .height(38.dp)
            .width(54.dp)
            .background(Color.Black, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}