package com.aipos.aipospm.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.UUID

/** Stores user-imported icons in app-private files so they remain available after restart. */
data class StoredVaultIcon(
    val id: String,
    val label: String,
    val file: File
)

object CustomIconStorage {
    private const val PREFIX = "custom:"
    private const val DIRECTORY = "custom_icons"
    private const val MAX_DIMENSION = 512

    fun isCustomIcon(id: String?): Boolean = id?.startsWith(PREFIX) == true

    fun list(context: Context): List<StoredVaultIcon> {
        val directory = File(context.filesDir, DIRECTORY)
        return directory.listFiles { file -> file.isFile && file.extension.equals("png", ignoreCase = true) }
            .orEmpty()
            .mapNotNull { file ->
                val separator = file.nameWithoutExtension.indexOf("__")
                if (separator <= 0) return@mapNotNull null
                val uuid = file.nameWithoutExtension.substring(0, separator)
                if (runCatching { UUID.fromString(uuid) }.isFailure) return@mapNotNull null
                val label = file.nameWithoutExtension.substring(separator + 2).replace('_', ' ').ifBlank { "Icon" }
                StoredVaultIcon(PREFIX + uuid, label, file)
            }
            .sortedBy { it.label.lowercase() }
    }

    fun imageFile(context: Context, id: String?): File? {
        if (id == null || !id.startsWith(PREFIX)) return null
        val uuid = id.removePrefix(PREFIX)
        if (runCatching { UUID.fromString(uuid) }.isFailure) return null
        return File(context.filesDir, DIRECTORY).listFiles { file ->
            file.isFile && file.name.startsWith("${uuid}__") && file.extension.equals("png", ignoreCase = true)
        }?.firstOrNull()
    }

    /** Imports one selected image, downsizing it to a practical icon size and storing a private PNG copy. */
    fun import(context: Context, uri: Uri): StoredVaultIcon? {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val boundsStream = resolver.openInputStream(uri) ?: return null
        boundsStream.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sampleSize = 1
        while (bounds.outWidth / sampleSize > MAX_DIMENSION || bounds.outHeight / sampleSize > MAX_DIMENSION) {
            sampleSize *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) } ?: return null
        val uuid = UUID.randomUUID().toString()
        val label = displayName(context, uri)
        val safeLabel = label
            .substringBeforeLast('.', label)
            .replace(Regex("[^A-Za-z0-9_-]"), "_")
            .trim('_')
            .take(36)
            .ifBlank { "icon" }
        val directory = File(context.filesDir, DIRECTORY).apply { mkdirs() }
        val destination = File(directory, "${uuid}__${safeLabel}.png")
        val saved = try {
            destination.outputStream().buffered().use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
        } catch (_: Exception) {
            false
        } finally {
            bitmap.recycle()
        }
        if (!saved) {
            destination.delete()
            return null
        }
        return StoredVaultIcon(PREFIX + uuid, label, destination)
    }

    fun delete(context: Context, id: String): Boolean = imageFile(context, id)?.delete() ?: false

    private fun displayName(context: Context, uri: Uri): String {
        val fromProvider = runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        }.getOrNull()
        return fromProvider?.substringBeforeLast('.', fromProvider)?.take(40)?.ifBlank { null } ?: "Icon"
    }
}
