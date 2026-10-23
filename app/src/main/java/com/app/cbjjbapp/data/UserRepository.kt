package com.app.cbjjbapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    suspend fun getCurrentProfile(): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val document = firestore.collection(USERS).document(uid).get().await()
        UserProfile(
            uid = document.getString("uid") ?: uid,
            nome = document.getString("nome").orEmpty(),
            email = document.getString("email").orEmpty(),
            doc = document.getString("doc").orEmpty(),
            equipe = document.getString("equipe").orEmpty(),
            tecnico = document.getString("tecnico").orEmpty(),
            nascimento = document.getString("nascimento").orEmpty(),
            genero = document.getString("genero") ?: document.getString("gênero").orEmpty(),
            peso = document.getString("peso").orEmpty(),
            telefone = document.getString("telefone").orEmpty(),
            faixa = document.getString("faixa").orEmpty(),
            graduacao = document.getString("graduacao").orEmpty(),
            ativacao = document.getString("ativacao") ?: document.getString("validacao").orEmpty(),
            carteiraValidaAte = document.getTimestamp("carteiraValidaAte"),
            acessoVitalicio = document.getBoolean("acessoVitalicio") ?: false,
            appsCarteira = (document.get("appsCarteira") as? List<*>)
                ?.filterIsInstance<String>()
                ?.map(String::lowercase)
                ?.toSet()
                .orEmpty(),
        )
    }

    private fun requireUserId(): String =
        requireNotNull(auth.currentUser?.uid) { "Usuário não autenticado" }

    private companion object {
        const val USERS = "Usuarios"
    }
}
