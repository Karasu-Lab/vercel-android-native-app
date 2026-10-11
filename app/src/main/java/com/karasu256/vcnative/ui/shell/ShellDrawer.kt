package com.karasu256.vcnative.ui.shell

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.NeuTheme
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private val DrawerWidth = 300.dp
private const val FlingVelocity = 1000f

@Composable
fun ShellDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val expansion = remember { Animatable(0f) }
    val spacing = NeuTheme.spacing

    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isClosed }
            .filter { it }
            .collect { expansion.snapTo(0f) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            BoxWithConstraints {
                val fullWidthPx = constraints.maxWidth
                NeuSurface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(lerp(DrawerWidth, maxWidth, expansion.value))
                        .pointerInput(fullWidthPx) {
                            val range = (fullWidthPx - DrawerWidth.toPx()).coerceAtLeast(1f)
                            val velocityTracker = VelocityTracker()
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                velocityTracker.resetTracking()
                                velocityTracker.addPointerInputChange(down)
                                var current = expansion.value
                                val drag = awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
                                    if (overSlop > 0f || current > 0f) {
                                        change.consume()
                                        current = (current + overSlop / range).coerceIn(0f, 1f)
                                    }
                                } ?: return@awaitEachGesture
                                scope.launch { expansion.snapTo(current) }
                                horizontalDrag(drag.id) { change ->
                                    velocityTracker.addPointerInputChange(change)
                                    current = (current + change.positionChange().x / range).coerceIn(0f, 1f)
                                    change.consume()
                                    scope.launch { expansion.snapTo(current) }
                                }
                                val velocity = velocityTracker.calculateVelocity().x
                                val target = when {
                                    velocity > FlingVelocity -> 1f
                                    velocity < -FlingVelocity -> 0f
                                    current >= 0.5f -> 1f
                                    else -> 0f
                                }
                                scope.launch { expansion.animateTo(target) }
                            }
                        },
                    shape = RectangleShape,
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.statusBars)
                                .windowInsetsPadding(
                                    WindowInsets.systemBars
                                        .union(WindowInsets.displayCutout)
                                        .only(WindowInsetsSides.Horizontal)
                                )
                                .padding(horizontal = spacing.lg),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NeuButton(
                                onClick = { scope.launch { drawerState.close() } },
                                modifier = Modifier.size(48.dp),
                                variant = NeuButtonVariant.Neutral,
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Image(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close_menu),
                                    colorFilter = ColorFilter.tint(LocalNeuContentColor.current),
                                )
                            }
                        }
                    }
                }
            }
        },
        content = content,
    )
}
