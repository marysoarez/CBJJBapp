package com.app.cbjjbapp.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.cbjjbapp.data.CertificateItem
import com.app.cbjjbapp.data.MediaRepository
import com.app.cbjjbapp.data.MediaType
import com.app.cbjjbapp.util.ImageBase64Codec
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CertificateScreenState(
    val loading: Boolean = false,
    val certificates: List<CertificateItem> = emptyList(),
    val message: String? = null,
)

@HiltViewModel
class CertificateViewModel @Inject constructor(
    private val media: MediaRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CertificateScreenState())
    val state: StateFlow<CertificateScreenState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, message = null)
            _state.value = media.loadCertificates().fold(
                onSuccess = { CertificateScreenState(certificates = it) },
                onFailure = { CertificateScreenState(message = it.message ?: "Falha ao carregar certificados") },
            )
        }
    }

    fun saveImage(base64: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, message = null)
            media.save(MediaType.CERTIFICATE, base64).fold(
                onSuccess = { load() },
                onFailure = { _state.value = _state.value.copy(loading = false, message = it.message ?: "Falha ao enviar certificado") },
            )
        }
    }

    fun encodeAndSaveCertificate(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            runCatching { ImageBase64Codec.encodeJpeg(contentResolver, uri, 1200, 1600) }
                .onSuccess(::saveImage)
                .onFailure { _state.value = _state.value.copy(loading = false, message = it.message ?: "Imagem inválida") }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            media.deleteCertificate(id).fold(
                onSuccess = { load() },
                onFailure = { _state.value = _state.value.copy(message = it.message ?: "Falha ao excluir certificado") },
            )
        }
    }

    fun requestActivation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, message = null)
            media.requestActivation(_state.value.certificates.map(CertificateItem::id)).fold(
                onSuccess = { protocol -> _state.value = _state.value.copy(loading = false, message = "Solicitação enviada. Protocolo: $protocol") },
                onFailure = { _state.value = _state.value.copy(loading = false, message = it.message ?: "Falha ao solicitar ativação") },
            )
        }
    }
}
