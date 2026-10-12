package com.karasu256.vcnative.ui.shell

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.BarSurface
import com.karasu256.vcnative.ui.theme.DrawerSurface
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.SectionHeader
import com.karasu256.vcnative.ui.theme.ToolbarButton
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private val DrawerWidth = 300.dp
private const val FlingVelocity = 1000f

@Composable
fun ShellDrawer(
    drawerState: DrawerState,
    avatar: ImageBitmap?,
    onUserClick: () -> Unit,
    onThemeClick: () -> Unit,
    onDeploymentsClick: () -> Unit,
    onLogsClick: () -> Unit,
    modifier: Modifier = Modifier,
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val expansion = remember { Animatable(0f) }
    val spacing = NeuTheme.spacing
    val horizontalInsets = WindowInsets.systemBars
        .union(WindowInsets.displayCutout)
        .only(WindowInsetsSides.Horizontal)

    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isClosed }
            .filter { it }
            .collect { expansion.snapTo(0f) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            BoxWithConstraints {
                val fullWidthPx = constraints.maxWidth
                DrawerSurface(
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
                                val drag =
                                    awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
                                        if (overSlop > 0f || current > 0f) {
                                            change.consume()
                                            current = (current + overSlop / range).coerceIn(0f, 1f)
                                        }
                                    } ?: return@awaitEachGesture
                                scope.launch { expansion.snapTo(current) }
                                horizontalDrag(drag.id) { change ->
                                    velocityTracker.addPointerInputChange(change)
                                    current =
                                        (current + change.positionChange().x / range).coerceIn(
                                            0f,
                                            1f
                                        )
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
                ) {
                    Column {
                        BarSurface(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.statusBars)
                                    .windowInsetsPadding(horizontalInsets)
                                    .padding(
                                        start = spacing.lg,
                                        end = spacing.lg,
                                        bottom = spacing.sm
                                    ),
                                horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ToolbarButton(onClick = { scope.launch { drawerState.close() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.close_menu),
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.menu),
                                    style = NeuTheme.typography.title,
                                    maxLines = 1,
                                )
                            }
                        }
                        SectionHeader(title = stringResource(R.string.drawer_section_application))
                        DrawerItem(
                            icon = Icons.Default.RocketLaunch,
                            label = stringResource(R.string.deployment_list),
                            onClick = onDeploymentsClick,
                        )
                        DrawerItem(
                            icon = Icons.Default.Description,
                            label = stringResource(R.string.app_logs),
                            onClick = onLogsClick,
                        )
                        Spacer(Modifier.weight(1f))
                        BarSurface(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .windowInsetsPadding(
                                        WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                                    )
                                    .windowInsetsPadding(horizontalInsets)
                                    .padding(horizontal = spacing.lg, vertical = spacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ToolbarButton(onClick = onUserClick) {
                                    UserAvatar(avatar = avatar, size = 36.dp)
                                }
                                ToolbarButton(onClick = onThemeClick) {
                                    Icon(
                                        imageVector = Icons.Default.Brightness6,
                                        contentDescription = stringResource(R.string.theme),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        content = content,
    )
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(text = label, maxLines = 1) },
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = { Icon(imageVector = icon, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

@Composable
fun UserAvatar(
    avatar: ImageBitmap?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    if (avatar != null) {
        Image(
            bitmap = avatar,
            contentDescription = stringResource(R.string.user),
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = stringResource(R.string.user),
            modifier = modifier.size(size),
        )
    }
}
