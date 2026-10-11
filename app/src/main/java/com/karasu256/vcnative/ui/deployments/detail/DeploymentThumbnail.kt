package com.karasu256.vcnative.ui.deployments.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.components.NeuSkeleton
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuSurfaceStyle
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun DeploymentThumbnail(
    thumbnail: ImageBitmap?,
    isLoading: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val shape = NeuTheme.shapes.lg
    val thumbnailModifier = modifier
        .fillMaxWidth()
        .aspectRatio(16f / 10f)

    when {
        isLoading -> NeuSkeleton(modifier = thumbnailModifier, shape = shape)
        thumbnail != null -> Image(
            bitmap = thumbnail,
            contentDescription = contentDescription,
            modifier = thumbnailModifier.clip(shape),
            contentScale = ContentScale.Crop,
        )
        else -> NeuSurface(
            modifier = thumbnailModifier,
            shape = shape,
            style = NeuSurfaceStyle.Inset,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = NeuTheme.colors.onSurfaceMuted,
                )
            }
        }
    }
}
