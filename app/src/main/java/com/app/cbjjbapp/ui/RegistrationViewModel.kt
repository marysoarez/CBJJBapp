package com.app.cbjjbapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.cbjjbapp.data.AuthRepository
import com.app.cbjjbapp.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()

    fun register(profile: UserProfile, password: String) {
        val requiredFields = listOf(
            profile.nome,
            profile.email,
            profile.equipe,
            profile.nascimento,
            profile.genero,
            profile.peso,
            profile.telefone,
            profile.faixa,
            profile.graduacao,
        )
        if (requiredFields.any(String::isBlank) || password.isBlank()) {
            _state.value = UiState.Error("Preencha todos os campos")
            return
        }
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repository.register(profile, password).fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(repository.registrationMessage(it)) },
            )
        }
    }
}
