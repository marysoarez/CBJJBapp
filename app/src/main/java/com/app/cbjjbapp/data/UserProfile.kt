package com.app.cbjjbapp.data

import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String = "",
    val nome: String = "",
    val email: String = "",
    val doc: String = "",
    val equipe: String = "",
    val tecnico: String = "",
    val nascimento: String = "",
    val genero: String = "",
    val peso: String = "",
    val telefone: String = "",
    val faixa: String = "",
    val graduacao: String = "",
    val ativacao: String = "",
    val carteiraValidaAte: Timestamp? = null,
    val acessoVitalicio: Boolean = false,
    val appsCarteira: Set<String> = emptySet(),
) {
    fun toFirestore(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "nome" to nome,
        "email" to email,
        "doc" to doc,
        "equipe" to equipe,
        "tecnico" to tecnico,
        "nascimento" to nascimento,
        "genero" to genero,
        "peso" to peso,
        "telefone" to telefone,
        "faixa" to faixa,
        "graduacao" to graduacao,
        "ativacao" to ativacao,
        "carteiraValidaAte" to carteiraValidaAte,
        "acessoVitalicio" to acessoVitalicio,
        "appsCarteira" to appsCarteira.toList(),
    )
}
