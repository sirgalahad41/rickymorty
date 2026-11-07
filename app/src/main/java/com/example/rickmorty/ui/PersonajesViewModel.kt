package com.example.rickmorty.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickmorty.data.PersonajesRepository
import com.example.rickmorty.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonajesViewModel(
    private val repository: PersonajesRepository =
        PersonajesRepository(RetrofitInstance.api)
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.obtenerPersonajes().fold(
                onSuccess = { UiState.Success(it.results) },
                onFailure = { UiState.Error("No se pudo cargar. Revisa Internet e intenta de nuevo.") }
            )
        }
    }
}
