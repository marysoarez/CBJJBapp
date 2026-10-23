package com.app.cbjjbapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    val isSignedIn: Boolean get() = auth.currentUser != null

    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun register(profile: UserProfile, password: String): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(profile.email.trim(), password).await()
        val user = checkNotNull(result.user)
        try {
            firestore.collection(USERS).document(user.uid).set(
                profile.copy(uid = user.uid).toFirestore(),
            ).await()
        } catch (error: Exception) {
            user.delete().await()
            throw error
        }
    }

    fun signOut() = auth.signOut()

    fun registrationMessage(error: Throwable): String = when (error) {
        is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
        is FirebaseAuthUserCollisionException -> "Este e-mail já foi cadastrado"
        is FirebaseAuthInvalidCredentialsException -> "E-mail inválido"
        else -> "Não foi possível realizar o cadastro"
    }

    private companion object {
        const val USERS = "Usuarios"
    }
}
