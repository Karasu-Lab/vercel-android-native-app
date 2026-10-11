package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.shell.UserAvatar
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun DeploymentInfoListTile(
    deployment: DeploymentInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = deployment.projectName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier.clickable(onClick = onClick),
        supportingContent = deployment.description?.let { description ->
            {
                Text(
                    text = description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                deployment.elapsedSeconds?.let { seconds ->
                    Text(
                        text = formatElapsed(seconds),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                DeploymentStateIndicator(state = deployment.state)
                deployment.creators.forEach { creator ->
                    UserAvatar(avatar = creator.avatar, size = 24.dp)
                }
            }
        },
    )
}

internal fun formatElapsed(seconds: Long): String {
    val minutes = seconds / 60
    val remainder = seconds % 60
    return if (minutes > 0) "${minutes}m ${remainder}s" else "${remainder}s"
}
