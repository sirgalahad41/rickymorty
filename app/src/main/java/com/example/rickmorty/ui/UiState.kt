package com.example.rickmorty.ui

import com.example.rickmorty.data.Personaje

sealed interface UiState {
    data object Loading : UiState
    data class Success(val items: List<Personaje>) : UiState
    data class Error(val message: String) : UiState
}

sealed interface DetalleUiState {
    data object Loading : DetalleUiState
    data class Success(val personaje: Personaje) : DetalleUiState
    data class Error(val message: String) : DetalleUiState
}
