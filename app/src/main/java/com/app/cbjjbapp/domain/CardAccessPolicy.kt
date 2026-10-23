package com.app.cbjjbapp.domain

import com.app.cbjjbapp.data.UserProfile
import java.text.Normalizer
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class CardApp(val key: String) {
    CBJJB("cbjjb"),
    IBCJJ("ibcjj"),
}

data class CardAccessDecision(val allowed: Boolean, val message: String)

object CardAccessPolicy {
    fun evaluate(
        profile: UserProfile,
        app: CardApp,
        today: LocalDate = LocalDate.now(),
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): CardAccessDecision {
        val appAllowed = profile.appsCarteira.isEmpty() || app.key in profile.appsCarteira
        if (!appAllowed) {
            return CardAccessDecision(false, "Carteirinha não liberada para este aplicativo")
        }
        if (profile.acessoVitalicio || profile.ativacao.normalized().contains("vitalicio")) {
            return CardAccessDecision(true, "Carteirinha vitalícia")
        }

        val expiration = profile.carteiraValidaAte?.toDate()?.toInstant()
            ?.atZone(zoneId)?.toLocalDate()
        if (expiration != null) {
            return if (expiration >= today) {
                CardAccessDecision(true, "Carteirinha válida até ${expiration.format(BRAZILIAN_DATE)}")
            } else {
                CardAccessDecision(false, "Carteirinha vencida em ${expiration.format(BRAZILIAN_DATE)}")
            }
        }

        // Compatibilidade com o campo textual usado pelas versões anteriores.
        if (profile.ativacao.contains(today.year.toString())) {
            return CardAccessDecision(true, "Carteirinha válida para ${today.year}")
        }
        return CardAccessDecision(false, "Carteirinha não ativada para ${today.year}")
    }

    private fun String.normalized(): String = Normalizer
        .normalize(lowercase(), Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")

    private val BRAZILIAN_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")
}
