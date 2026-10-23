package com.app.cbjjbapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

enum class MediaType { PROFILE, CERTIFICATE }

data class CertificateItem(
    val id: String,
    val name: String,
    val imageBase64: String,
)

@Singleton
class MediaRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    suspend fun load(type: MediaType): Result<String?> = runCatching {
        when (type) {
            MediaType.PROFILE -> userDocument().get().await().getString(PROFILE_BASE64)
            MediaType.CERTIFICATE -> certificatesCollection()
                .orderBy(CREATED_AT, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.getString(IMAGE_BASE64)
        }
    }

    suspend fun save(type: MediaType, base64: String): Result<Unit> = runCatching {
        require(base64.length <= MAX_BASE64_CHARACTERS) { "A imagem ficou grande demais" }
        when (type) {
            MediaType.PROFILE -> userDocument().set(
                mapOf(
                    PROFILE_BASE64 to base64,
                    PROFILE_UPDATED_AT to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            ).await()
            MediaType.CERTIFICATE -> certificatesCollection().add(
                mapOf(
                    NAME to "certificado.jpg",
                    IMAGE_BASE64 to base64,
                    MIME_TYPE to "image/jpeg",
                    CREATED_AT to FieldValue.serverTimestamp(),
                ),
            ).await()
        }
        Unit
    }

    suspend fun loadCertificates(): Result<List<CertificateItem>> = runCatching {
        certificatesCollection()
            .orderBy(CREATED_AT, Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val image = document.getString(IMAGE_BASE64) ?: return@mapNotNull null
                CertificateItem(
                    id = document.id,
                    name = document.getString(NAME) ?: "Certificado",
                    imageBase64 = image,
                )
            }
    }

    suspend fun deleteCertificate(id: String): Result<Unit> = runCatching {
        certificatesCollection().document(id).delete().await()
    }

    suspend fun requestActivation(certificateIds: List<String>): Result<String> = runCatching {
        require(certificateIds.isNotEmpty()) { "Adicione ao menos um certificado ou comprovante" }
        val user = requireNotNull(auth.currentUser) { "Usuário não autenticado" }
        firestore.collection(ACTIVATION_REQUESTS).add(
            mapOf(
                "userId" to user.uid,
                "email" to user.email,
                "certificateIds" to certificateIds,
                "certificateCount" to certificateIds.size,
                "status" to "pendente",
                "requestedAt" to FieldValue.serverTimestamp(),
                "sourceApp" to "cbjjb",
            ),
        ).await().id
    }

    private fun userDocument() = firestore.collection(USERS).document(requireUserId())
    private fun certificatesCollection() = userDocument().collection(CERTIFICATES)
    private fun requireUserId() = requireNotNull(auth.currentUser?.uid) { "Usuário não autenticado" }

    private companion object {
        const val USERS = "Usuarios"
        const val CERTIFICATES = "certificados"
        const val ACTIVATION_REQUESTS = "SolicitacoesAtivacao"
        const val PROFILE_BASE64 = "profileImageBase64"
        const val PROFILE_UPDATED_AT = "profileImageUpdatedAt"
        const val IMAGE_BASE64 = "imageBase64"
        const val NAME = "name"
        const val MIME_TYPE = "mimeType"
        const val CREATED_AT = "createdAt"
        const val MAX_BASE64_CHARACTERS = 750_000
    }
}
