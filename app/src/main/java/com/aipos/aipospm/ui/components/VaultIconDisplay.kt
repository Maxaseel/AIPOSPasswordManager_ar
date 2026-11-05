package com.aipos.aipospm.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun VaultIconDisplay(
    iconId: String?,
    fallback: ImageVector? = null,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) {
    val context = LocalContext.current
    val imageFile = remember(iconId) { CustomIconStorage.imageFile(context, iconId) }
    val bitmap = remember(imageFile?.absolutePath) {
        imageFile?.let { BitmapFactory.decodeFile(it.absolutePath)?.asImageBitmap() }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        val vector = remember(iconId, fallback) { VaultIconRegistry.getIcon(iconId) ?: fallback }
        if (vector != null) {
            Icon(
                imageVector = vector,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint
            )
        }
    }
}
