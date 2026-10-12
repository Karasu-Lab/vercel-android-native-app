package com.karasu256.vcnative.ui.deployments.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.karasu256.vcnative.ui.theme.ImagePlaceholder
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Skeleton

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
        isLoading -> Skeleton(modifier = thumbnailModifier, shape = shape)
        thumbnail != null -> Image(
            bitmap = thumbnail,
            contentDescription = contentDescription,
            modifier = thumbnailModifier.clip(shape),
            contentScale = ContentScale.Crop,
        )
        else -> ImagePlaceholder(modifier = thumbnailModifier)
    }
}
