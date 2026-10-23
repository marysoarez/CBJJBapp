package com.app.cbjjbapp.domain

import com.app.cbjjbapp.data.UserProfile
import com.google.firebase.Timestamp
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class CardAccessPolicyTest {
    private val today = LocalDate.of(2026, 10, 16)
    private val zone = ZoneId.of("America/Sao_Paulo")

    @Test
    fun `annual access is valid through expiration date`() {
        val expiration = Date.from(today.atStartOfDay(zone).toInstant())
        val profile = UserProfile(
            carteiraValidaAte = Timestamp(expiration),
            appsCarteira = setOf("cbjjb"),
        )

        assertTrue(CardAccessPolicy.evaluate(profile, CardApp.CBJJB, today, zone).allowed)
    }

    @Test
    fun `lifetime access still respects enabled apps`() {
        val profile = UserProfile(acessoVitalicio = true, appsCarteira = setOf("ibcjj"))

        assertFalse(CardAccessPolicy.evaluate(profile, CardApp.CBJJB, today, zone).allowed)
        assertTrue(CardAccessPolicy.evaluate(profile, CardApp.IBCJJ, today, zone).allowed)
    }

    @Test
    fun `legacy activation remains compatible`() {
        val profile = UserProfile(ativacao = "Ativo 2026")

        assertTrue(CardAccessPolicy.evaluate(profile, CardApp.CBJJB, today, zone).allowed)
    }
}
