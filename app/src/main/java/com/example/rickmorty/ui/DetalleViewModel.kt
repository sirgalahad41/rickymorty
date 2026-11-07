package com.example.rickmorty.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickmorty.data.PersonajesRepository
import com.example.rickmorty.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repository: PersonajesRepository =
        PersonajesRepository(RetrofitInstance.api)
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun cargar(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetalleUiState.Loading
            _uiState.value = repository.obtenerPersonaje(id).fold(
                onSuccess = { DetalleUiState.Success(it) },
                onFailure = { DetalleUiState.Error("No se pudo cargar el personaje. Reintenta.") }
            )
        }
    }
}
