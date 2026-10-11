package com.karasu256.vcnative.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import com.karasu256.vcnative.ui.theme.NeuTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun NeuSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val style = NeuTheme.snackbar
    AnimatedContent(
        targetState = hostState.currentSnackbarData,
        modifier = modifier,
        transitionSpec = { (style.enter togetherWith style.exit).using(SizeTransform(clip = false)) },
        contentAlignment = Alignment.TopCenter,
        label = "snackbar",
    ) { data ->
        if (data != null) NeuSnackbar(data = data)
    }
}

@Composable
fun NeuSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val style = NeuTheme.snackbar
    val spacing = NeuTheme.spacing
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    var height by remember { mutableIntStateOf(0) }
    val dragState = rememberDraggableState { delta ->
        scope.launch { offsetY.snapTo((offsetY.value + delta).coerceAtMost(0f)) }
    }

    NeuSurface(
        modifier = modifier
            .onSizeChanged { height = it.height }
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .graphicsLayer {
                alpha = if (height > 0) 1f - (-offsetY.value / height).coerceIn(0f, 1f) else 1f
            }
            .draggable(
                state = dragState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    val dismissed = offsetY.value < -height * style.dismissThreshold ||
                        velocity < -style.dismissVelocity
                    if (dismissed) {
                        offsetY.animateTo(-height.toFloat())
                        data.dismiss()
                    } else {
                        offsetY.animateTo(0f)
                    }
                },
            ),
        shape = style.shape,
        depth = style.depth,
    ) {
        Row(
            modifier = Modifier.padding(style.contentPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeuText(text = data.visuals.message)
            data.visuals.actionLabel?.let { label ->
                NeuText(
                    text = label,
                    modifier = Modifier.clickable(role = Role.Button, onClick = data::performAction),
                    color = NeuTheme.colors.primary,
                    style = NeuTheme.typography.button,
                )
            }
        }
    }
}
