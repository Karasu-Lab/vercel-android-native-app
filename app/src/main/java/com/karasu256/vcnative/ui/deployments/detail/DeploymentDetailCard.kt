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
import com.karasu256.vcnative.ui.components.NeuCard
import com.karasu256.vcnative.ui.components.NeuSkeleton
import com.karasu256.vcnative.ui.components.NeuText
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
    NeuCard(modifier = modifier.fillMaxWidth()) {
        if (detail == null) {
            repeat(6) {
                NeuSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
            }
            return@NeuCard
        }

        DetailRow(label = R.string.label_status) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NeuTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DeploymentStateIndicator(state = detail.state)
                NeuText(text = stringResource(detail.state.labelRes))
                detail.elapsedSeconds?.let { seconds ->
                    NeuText(
                        text = formatElapsed(seconds),
                        color = NeuTheme.colors.onSurfaceMuted,
                    )
                }
            }
        }
        detail.target?.let { target ->
            DetailRow(label = R.string.label_environment) {
                NeuText(
                    text = stringResource(
                        if (target == "production") R.string.environment_production else R.string.environment_preview
                    )
                )
            }
        }
        detail.branch?.let { branch ->
            DetailRow(label = R.string.label_branch) { NeuText(text = branch) }
        }
        detail.commitMessage?.let { message ->
            DetailRow(label = R.string.label_commit) {
                Column {
                    NeuText(text = message)
                    detail.commitSha?.let { sha ->
                        NeuText(text = sha, color = NeuTheme.colors.onSurfaceMuted)
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
                    NeuText(text = creator.username)
                }
            }
        }
        DetailRow(label = R.string.label_created_at) {
            NeuText(
                text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(Date(detail.createdAt))
            )
        }
        if (detail.domains.isNotEmpty()) {
            DetailRow(label = R.string.label_domains) {
                Column {
                    detail.domains.forEach { domain -> NeuText(text = domain) }
                }
            }
        }
        if (detail.regions.isNotEmpty()) {
            DetailRow(label = R.string.label_regions) {
                NeuText(text = detail.regions.joinToString())
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
        NeuText(
            text = stringResource(label),
            style = NeuTheme.typography.labelCaps,
            color = NeuTheme.colors.onSurfaceMuted,
        )
        content()
    }
}
