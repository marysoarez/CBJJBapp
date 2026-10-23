package com.app.cbjjbapp.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImageBase64Codec {
    private const val MAX_BASE64_CHARACTERS = 700_000

    suspend fun encodeJpeg(
        resolver: ContentResolver,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int,
    ): String = withContext(Dispatchers.IO) {
        val source = resolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Não foi possível abrir a imagem" }
            requireNotNull(BitmapFactory.decodeStream(input)) { "Formato de imagem inválido" }
        }
        val scaled = source.scaleDown(maxWidth, maxHeight)
        if (scaled !== source) source.recycle()

        try {
            var quality = 82
            var encoded: String
            do {
                val bytes = ByteArrayOutputStream().use { output ->
                    check(scaled.compress(Bitmap.CompressFormat.JPEG, quality, output))
                    output.toByteArray()
                }
                encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
                quality -= 8
            } while (encoded.length > MAX_BASE64_CHARACTERS && quality >= 34)

            require(encoded.length <= MAX_BASE64_CHARACTERS) {
                "A imagem é muito grande. Escolha uma imagem menor."
            }
            encoded
        } finally {
            scaled.recycle()
        }
    }

    fun decode(base64: String): Bitmap? = runCatching {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()

    private fun Bitmap.scaleDown(maxWidth: Int, maxHeight: Int): Bitmap {
        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height, 1f)
        if (ratio >= 1f) return this
        return Bitmap.createScaledBitmap(this, (width * ratio).toInt(), (height * ratio).toInt(), true)
    }
}
