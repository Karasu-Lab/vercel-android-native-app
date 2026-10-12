package com.karasu256.vcnative.ui.envvars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.ui.components.ListItemSwipeAction
import com.karasu256.vcnative.ui.components.SwipeDirection
import com.karasu256.vcnative.ui.components.SwipeableListItem
import com.karasu256.vcnative.ui.components.alignment
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.SwipeActionBackground

@Composable
private fun EnvironmentVariableSummary(variable: EnvironmentVariable) {
    val spacing = NeuTheme.spacing
    val environments = variable.targets
        .sortedBy { it.ordinal }
        .map { stringResource(it.labelRes) }
        .joinToString(" / ")

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = environments, maxLines = 1)
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(12.dp)
                .background(NeuTheme.colors.outline)
        )
        Text(
            text = if (variable.isSecret) {
                stringResource(R.string.label_secret)
            } else {
                variable.value.orEmpty()
            },
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private class RemoveSwipeAction(
    private val onRemove: () -> Unit,
) : ListItemSwipeAction(SwipeDirection.RightToLeft) {
    override fun execute() = onRemove()
}

/**
 * Right-to-left swipe asks for removal; the caller is expected to confirm it. A variable linked
 * from a shared variable is unlinked instead of deleted.
 */
@Composable
fun SwipeableEnvironmentVariableTile(
    variable: EnvironmentVariable,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = remember(variable, onRemove) { listOf(RemoveSwipeAction(onRemove)) }

    SwipeableListItem(
        actions = actions,
        modifier = modifier,
        background = { swipeAction ->
            SwipeActionBackground(
                icon = if (variable.isShared) Icons.Default.LinkOff else Icons.Default.Delete,
                label = stringResource(
                    if (variable.isShared) R.string.swipe_unlink else R.string.swipe_delete
                ),
                alignment = swipeAction.direction.alignment,
                destructive = true,
            )
        },
    ) {
        ListItem(
            headlineContent = { Text(text = variable.key, maxLines = 1) },
            supportingContent = { EnvironmentVariableSummary(variable = variable) },
            trailingContent = if (variable.isShared) {
                null
            } else {
                {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_environment_variable),
                        )
                    }
                }
            },
        )
    }
}
