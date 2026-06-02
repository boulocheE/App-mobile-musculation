package com.example.musculation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Warning
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initDatabase(context)
    }

    val scrollState = rememberScrollState()

    // États pour les champs de saisie
    var nomExerciceActuel by remember { mutableStateOf("") }
    var poidsActuel by remember { mutableStateOf("") }
    var repsActuelles by remember { mutableStateOf("") }
    val seriesExoActuel = remember { mutableStateListOf<Pair<String, String>>() }

    // États pour les boîtes de dialogue
    var afficherDialogueTitre by remember { mutableStateOf(false) }
    var saisieTitreSeance by remember { mutableStateOf("") }

    var afficherConfirmationAnnulation by remember { mutableStateOf(false) }

    BackHandler(enabled = viewModel.isWorkoutActive) {
        afficherConfirmationAnnulation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        if (!viewModel.isWorkoutActive) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Prêt à t'entraîner ?", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "Enregistre tes séries pour suivre ta progression.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val dateDuJour = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()).uppercase()
                        saisieTitreSeance = "Entraînement du $dateDuJour"
                        afficherDialogueTitre = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(text = "Commencer une séance", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            if (afficherDialogueTitre) {
                AlertDialog(
                    onDismissRequest = { afficherDialogueTitre = false },
                    title = { Text(text = "Nouvel entraînement", fontWeight = FontWeight.Bold) },
                    text = {
                        OutlinedTextField(
                            value = saisieTitreSeance,
                            onValueChange = { saisieTitreSeance = it },
                            label = { Text("Nom de la séance") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    },
                    confirmButton = {
                        Button(onClick = { viewModel.lancerNouvelleSeance(saisieTitreSeance); afficherDialogueTitre = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text("Valider et démarrer", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { afficherDialogueTitre = false }) { Text("Annuler", color = MaterialTheme.colorScheme.error) }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "SÉANCE EN COURS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.5.sp)
                        BasicTextField(
                            value = viewModel.titreSeance,
                            onValueChange = { viewModel.titreSeance = it },
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Black),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }

                    IconButton(onClick = { afficherConfirmationAnnulation = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Annuler", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.weight(1f).verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(value = nomExerciceActuel, onValueChange = { nomExerciceActuel = it }, label = { Text("Nom de l'exercice (ex: Développé Couché)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
                            Spacer(modifier = Modifier.height(14.dp))

                            // En-têtes
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "SÉRIE", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                Text(text = "KG", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                Text(text = "REPS", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.width(44.dp))
                            }

                            // Séries en cours de saisie
                            seriesExoActuel.forEachIndexed { index, serie ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Box(modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) { Text(text = "${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                    }
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) { BadgeInfoHistorique(text = serie.first) }
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) { BadgeInfoHistorique(text = serie.second) }
                                    Spacer(modifier = Modifier.width(44.dp))
                                }
                            }

                            // Ligne de saisie active
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Box(modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) { Text(text = "${seriesExoActuel.size + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                                }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) { MiniTextFieldSaisie(value = poidsActuel, onValueChange = { poidsActuel = it }) }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) { MiniTextFieldSaisie(value = repsActuelles, onValueChange = { repsActuelles = it }) }
                                Box(modifier = Modifier.width(44.dp), contentAlignment = Alignment.CenterEnd) {
                                    IconButton(onClick = { if (poidsActuel.isNotBlank() && repsActuelles.isNotBlank()) { seriesExoActuel.add(Pair(poidsActuel, repsActuelles)); poidsActuel = ""; repsActuelles = "" } }, modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp)) }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(onClick = { if (nomExerciceActuel.isNotBlank() && seriesExoActuel.isNotEmpty()) { viewModel.ajouterExerciceAMaSeance(nomExerciceActuel, seriesExoActuel.toList()); nomExerciceActuel = ""; seriesExoActuel.clear() } }, modifier = Modifier.align(Alignment.End), enabled = nomExerciceActuel.isNotBlank() && seriesExoActuel.isNotEmpty()) { Text("Ajouter cet exercice", fontWeight = FontWeight.Bold) }
                        }
                    }

                    if (viewModel.exercicesEnCours.isNotEmpty()) {
                        Text(text = "Exercices validés", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                        viewModel.exercicesEnCours.forEach { (nom, series) ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = nom, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    series.forEachIndexed { idx, serie -> Text(text = "Série ${idx + 1} :  ${serie.first} kg  x  ${serie.second} reps", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { viewModel.enregistrerSeance() }, modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary), enabled = viewModel.exercicesEnCours.isNotEmpty()) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Terminer et Sauvegarder", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (afficherConfirmationAnnulation) {
            AlertDialog(
                onDismissRequest = { afficherConfirmationAnnulation = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                title = { Text(text = "Abandonner la séance ?", fontWeight = FontWeight.Bold) },
                text = { Text("Toutes tes données saisies pour cet entraînement seront définitivement perdues.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.annulerSeance()
                            afficherConfirmationAnnulation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Oui, abandonner", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { afficherConfirmationAnnulation = false }) {
                        Text("Continuer l'effort", color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun MiniTextFieldSaisie(value: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .width(54.dp)
            .background(Color.Black, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}