package com.example.rickmorty

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickmorty.data.Personaje
import com.example.rickmorty.ui.DetalleUiState
import com.example.rickmorty.ui.DetalleViewModel
import com.example.rickmorty.ui.PersonajesViewModel
import com.example.rickmorty.ui.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RickMortyApp() }
    }
}

@Composable
fun RickMortyApp() {
    val navController = rememberNavController()
    MaterialTheme {
        NavHost(navController = navController, startDestination = "lista") {
            composable("lista") {
                ListaScreen(onPersonajeClick = { id ->
                    navController.navigate("detalle/$id")
                })
            }
            composable(
                route = "detalle/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id") ?: 1
                DetalleScreen(id = id, onBack = { navController.popBackStack() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    onPersonajeClick: (Int) -> Unit,
    vm: PersonajesViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        TopAppBar(title = { Text("Rick & Morty · Personajes") })
    }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val current = state) {
                UiState.Loading -> {
                    Spacer(Modifier.padding(12.dp))
                    CircularProgressIndicator()
                    Text("Cargando personajes...")
                }
                is UiState.Error -> {
                    Text(current.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = vm::cargar) { Text("Reintentar") }
                }
                is UiState.Success -> {
                    Button(onClick = vm::cargar) { Text("Recargar lista") }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(current.items, key = { it.id }) { personaje ->
                            PersonajeRow(personaje, onClick = { onPersonajeClick(personaje.id) })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PersonajeRow(personaje: Personaje, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = personaje.image,
                contentDescription = "Imagen de ${personaje.name}",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    personaje.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Estado: ${personaje.status}")
                Text("Especie: ${personaje.species}")
                Text("ID: ${personaje.id}")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    id: Int,
    onBack: () -> Unit,
    vm: DetalleViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(id) { vm.cargar(id) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Detalle del personaje") })
    }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onBack) { Text("Volver") }
            when (val current = state) {
                DetalleUiState.Loading -> CircularProgressIndicator()
                is DetalleUiState.Error -> Text(current.message, color = MaterialTheme.colorScheme.error)
                is DetalleUiState.Success -> DetalleContent(current.personaje)
            }
        }
    }
}


@Composable
fun DetalleContent(personaje: Personaje) {
    AsyncImage(
        model = personaje.image,
        contentDescription = "Imagen de ${personaje.name}",
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentScale = ContentScale.Fit
    )

    Text(
        personaje.name,
        style = MaterialTheme.typography.headlineMedium
    )

    Text("Estado: ${personaje.status}")
    Text("Especie: ${personaje.species}")
    Text("ID: ${personaje.id}")
}

