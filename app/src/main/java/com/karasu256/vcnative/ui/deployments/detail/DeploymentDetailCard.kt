package com.karasu256.vcnative.ui.deployments.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.ContentCard
import com.karasu256.vcnative.ui.theme.Skeleton
import androidx.compose.material3.Text
import com.karasu256.vcnative.ui.deployments.DeploymentStateIndicator
import com.karasu256.vcnative.ui.deployments.formatElapsed
import com.karasu256.vcnative.ui.deployments.labelRes
import com.karasu256.vcnative.ui.shell.UserAvatar
import com.karasu256.vcnative.ui.theme.NeuTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun DeploymentDetailCard(
    detail: DeploymentDetail?,
    modifier: Modifier = Modifier,
) {
    ContentCard(modifier = modifier.fillMaxWidth()) {
        if (detail == null) {
            repeat(6) {
                Skeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
            }
            return@ContentCard
        }

        DetailRow(label = R.string.label_status) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DeploymentStateIndicator(state = detail.state)
                Text(text = stringResource(detail.state.labelRes))
                detail.elapsedSeconds?.let { seconds ->
                    Text(
                        text = formatElapsed(seconds),
                        color = NeuTheme.colors.onSurfaceMuted,
                    )
                }
            }
        }
        detail.target?.let { target ->
            DetailRow(label = R.string.label_environment) {
                Text(
                    text = stringResource(
                        if (target == "production") R.string.environment_production else R.string.environment_preview
                    )
                )
            }
        }
        detail.branch?.let { branch ->
            DetailRow(label = R.string.label_branch) { Text(text = branch) }
        }
        detail.commitMessage?.let { message ->
            DetailRow(label = R.string.label_commit) {
                Column {
                    Text(text = message)
                    detail.commitSha?.let { sha ->
                        Text(text = sha, color = NeuTheme.colors.onSurfaceMuted)
                    }
                }
            }
        }
        detail.creator?.let { creator ->
            DetailRow(label = R.string.label_creator) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserAvatar(avatar = creator.avatar, size = 24.dp)
                    Text(text = creator.username)
                }
            }
        }
        DetailRow(label = R.string.label_created_at) {
            Text(
                text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(Date(detail.createdAt))
            )
        }
        if (detail.domains.isNotEmpty()) {
            DetailRow(label = R.string.label_domains) {
                Column {
                    detail.domains.forEach { domain -> Text(text = domain) }
                }
            }
        }
        if (detail.regions.isNotEmpty()) {
            DetailRow(label = R.string.label_regions) {
                Text(text = detail.regions.joinToString())
            }
        }
    }
}

@Composable
private fun DetailRow(
    @StringRes label: Int,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(NeuTheme.spacing.xs)) {
        Text(
            text = stringResource(label),
            style = NeuTheme.typography.labelCaps,
            color = NeuTheme.colors.onSurfaceMuted,
        )
        content()
    }
}
