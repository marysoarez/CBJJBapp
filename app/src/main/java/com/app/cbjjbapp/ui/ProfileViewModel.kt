package com.app.cbjjbapp.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.cbjjbapp.data.AuthRepository
import com.app.cbjjbapp.data.MediaRepository
import com.app.cbjjbapp.data.MediaType
import com.app.cbjjbapp.data.UserProfile
import com.app.cbjjbapp.data.UserRepository
import com.app.cbjjbapp.domain.CardAccessDecision
import com.app.cbjjbapp.domain.CardAccessPolicy
import com.app.cbjjbapp.domain.CardApp
import com.app.cbjjbapp.util.ImageBase64Codec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ProfileScreenState(
    val loading: Boolean = false,
    val profile: UserProfile? = null,
    val imageBase64: String? = null,
    val access: CardAccessDecision? = null,
    val message: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val users: UserRepository,
    private val media: MediaRepository,
    private val auth: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileScreenState())
    val state: StateFlow<ProfileScreenState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, message = null)
            val profile = users.getCurrentProfile()
            val image = media.load(MediaType.PROFILE)
            _state.value = ProfileScreenState(
                profile = profile.getOrNull(),
                imageBase64 = image.getOrNull(),
                access = profile.getOrNull()?.let { CardAccessPolicy.evaluate(it, CardApp.CBJJB) },
                message = profile.exceptionOrNull()?.message ?: image.exceptionOrNull()?.message,
            )
        }
    }

    fun saveImage(base64: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, message = null)
            _state.value = media.save(MediaType.PROFILE, base64).fold(
                onSuccess = { _state.value.copy(loading = false, imageBase64 = base64, message = "Foto atualizada") },
                onFailure = { _state.value.copy(loading = false, message = it.message ?: "Falha ao enviar imagem") },
            )
        }
    }

    fun encodeAndSaveProfileImage(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            runCatching { ImageBase64Codec.encodeJpeg(contentResolver, uri, 720, 720) }
                .onSuccess(::saveImage)
                .onFailure { _state.value = _state.value.copy(loading = false, message = it.message ?: "Imagem inválida") }
        }
    }

    fun signOut() = auth.signOut()
}
