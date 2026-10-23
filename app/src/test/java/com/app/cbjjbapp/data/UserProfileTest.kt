package com.app.cbjjbapp.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileTest {
    @Test
    fun `firestore map preserves legacy field names and never contains password`() {
        val profile = UserProfile(nome = "Maria", genero = "Feminino", appsCarteira = setOf("cbjjb"))

        val result = profile.toFirestore()

        assertEquals("Maria", result["nome"])
        assertEquals("Feminino", result["genero"])
        assertEquals(listOf("cbjjb"), result["appsCarteira"])
        assertFalse(result.containsKey("senha"))
    }
}
