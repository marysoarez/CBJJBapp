package com.app.cbjjbapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.cbjjbapp.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()
    val isSignedIn: Boolean get() = repository.isSignedIn

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = UiState.Error("Preencha todos os campos")
            return
        }
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repository.signIn(email, password).fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error("E-mail ou senha incorretos") },
            )
        }
    }
}
