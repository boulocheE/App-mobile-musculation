package com.example.musculation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musculation.data.SeanceComplete


@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.chargerDonnees(context)
    }

    val listeSeances by viewModel.historiqueSeances.collectAsState()
    var seanceSelectionnee by remember { mutableStateOf<SeanceComplete?>(null) }


    BackHandler(enabled = seanceSelectionnee != null) {
        seanceSelectionnee = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        if (seanceSelectionnee == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column {
                    Text(
                        text = "ANCIENS ENTRAÎNEMENTS",
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

                if (listeSeances.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aucune séance enregistrée pour le moment.\nTes futurs entraînements s'afficheront ici !️",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(listeSeances) { seanceComplete ->
                            ItemSeanceHistoriquePro(seanceComplete = seanceComplete, codeClick = {
                                seanceSelectionnee = seanceComplete
                            })
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
}

@Composable
fun ItemSeanceHistoriquePro(seanceComplete: SeanceComplete, codeClick: () -> Unit) {
    val seance = seanceComplete.seance
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
            .width(50.dp)
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


@Composable
fun SeanceDetailView(
    seanceComplete: SeanceComplete,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSaveClick: (SeanceComplete) -> Unit
) {
    val detailScrollState = rememberScrollState()
    val seanceInfo = seanceComplete.seance

    var modeEdition by remember { mutableStateOf(false) }

    var titreSeance by remember(seanceComplete, modeEdition) { mutableStateOf(seanceInfo.titre) }

    val nomsExercices = remember(seanceComplete, modeEdition) {
        seanceComplete.exercices.associate { it.exercice.id to mutableStateOf(it.exercice.nom) }
    }
    val poidsSeries = remember(seanceComplete, modeEdition) {
        seanceComplete.exercices.flatMap { it.series }.associate { it.id to mutableStateOf(it.poids) }
    }
    val repsSeries = remember(seanceComplete, modeEdition) {
        seanceComplete.exercices.flatMap { it.series }.associate { it.id to mutableStateOf(it.reps) }
    }

    var afficherDialogSuppression by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = if (modeEdition) "Modifier la séance" else "Détails",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (!modeEdition) {
                    Row {
                        IconButton(onClick = { modeEdition = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { afficherDialogSuppression = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { modeEdition = false }) {
                            Text("Annuler", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = {
                                val seanceModifiee = SeanceComplete(
                                    seance = seanceComplete.seance.copy(titre = titreSeance),
                                    exercices = seanceComplete.exercices.map { exo ->
                                        exo.copy(
                                            exercice = exo.exercice.copy(nom = nomsExercices[exo.exercice.id]?.value ?: exo.exercice.nom),
                                            series = exo.series.map { serie ->
                                                serie.copy(
                                                    poids = poidsSeries[serie.id]?.value ?: serie.poids,
                                                    reps = repsSeries[serie.id]?.value ?: serie.reps
                                                )
                                            }
                                        )
                                    }
                                )
                                onSaveClick(seanceModifiee)
                                modeEdition = false
                            }
                        ) {
                            Text("Valider", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(detailScrollState).padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column {
                    if (modeEdition) {
                        OutlinedTextField(
                            value = titreSeance,
                            onValueChange = { titreSeance = it },
                            label = { Text("Nom de la séance") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Text(text = seanceInfo.titre, fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = seanceInfo.date,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Le point de séparation
                        Text(
                            text = "  •  ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = seanceInfo.duree,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Liste des exercices
                seanceComplete.exercices.forEach { exerciceAvecSeries ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            if (modeEdition) {
                                OutlinedTextField(
                                    value = nomsExercices[exerciceAvecSeries.exercice.id]?.value ?: "",
                                    onValueChange = { nomsExercices[exerciceAvecSeries.exercice.id]?.value = it },
                                    label = { Text("Nom de l'exercice") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else {
                                Text(text = exerciceAvecSeries.exercice.nom, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "SÉRIE", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                Text(text = "KG", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                Text(text = "REPS", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }

                            // Liste des séries
                            exerciceAvecSeries.series.forEach { serie ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {

                                    // Index Série
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Box(modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                                            Text(text = "${serie.numero}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    // Poids
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        if (modeEdition) {
                                            MiniTextFieldEdition(
                                                value = poidsSeries[serie.id]?.value ?: "",
                                                onValueChange = { poidsSeries[serie.id]?.value = it }
                                            )
                                        } else {
                                            BadgeInfoHistorique(text = serie.poids)
                                        }
                                    }

                                    // Reps
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        if (modeEdition) {
                                            MiniTextFieldEdition(
                                                value = repsSeries[serie.id]?.value ?: "",
                                                onValueChange = { repsSeries[serie.id]?.value = it }
                                            )
                                        } else {
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

        // Pop-up de suppression
        if (afficherDialogSuppression) {
            AlertDialog(
                onDismissRequest = { afficherDialogSuppression = false },
                title = { Text(text = "Supprimer la séance ?", fontWeight = FontWeight.Bold) },
                text = { Text("Cette action effacera définitivement l'entraînement de l'historique.") },
                confirmButton = {
                    Button(onClick = { onDeleteClick(); afficherDialogSuppression = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("Supprimer", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { afficherDialogSuppression = false }) { Text("Annuler", color = MaterialTheme.colorScheme.onBackground) }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}


@Composable
fun MiniTextFieldEdition(value: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .height(38.dp)
            .width(54.dp)
            .background(Color.Black, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )
    }
}