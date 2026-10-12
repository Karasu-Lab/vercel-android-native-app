package com.karasu256.vcnative.ui.deployments.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.Branch
import com.karasu256.vcnative.data.model.BranchCommit
import com.karasu256.vcnative.ui.theme.ChipRow
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Panel
import com.karasu256.vcnative.ui.theme.PrimaryButton
import com.karasu256.vcnative.ui.theme.SecondaryButton
import com.karasu256.vcnative.ui.theme.Skeleton
import com.karasu256.vcnative.ui.theme.ToggleChip
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeploymentSheet(
    state: CreateDeploymentUiState,
    onProjectClick: () -> Unit,
    onBranchToggle: (Branch) -> Unit,
    onGitHubClick: () -> Unit,
    onDeploy: () -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    val spacing = NeuTheme.spacing

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = spacing.lg, end = spacing.lg, bottom = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            SecondaryButton(
                onClick = onProjectClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.selectedProject?.let { ProjectIcon(project = it, size = 24.dp) }
                Text(
                    text = state.selectedProject?.project?.name
                        ?: stringResource(R.string.select_project),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }

            BranchCommitInfo(
                branch = state.selectedBranch,
                commit = state.branchCommit,
                isLoading = state.isBranchCommitLoading,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val branches = state.branches
                if (state.selectedProject != null && branches == null) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        repeat(3) {
                            Skeleton(modifier = Modifier.size(width = 80.dp, height = 32.dp))
                        }
                    }
                } else {
                    ChipRow(modifier = Modifier.weight(1f)) {
                        items(items = branches.orEmpty(), key = { it.name }) { branch ->
                            ToggleChip(
                                label = branch.name,
                                selected = branch.name == state.selectedBranch?.name,
                                onToggle = { onBranchToggle(branch) },
                            )
                        }
                    }
                }
                GitHubLinkButton(isLinked = state.isGitHubLinked, onClick = onGitHubClick)
            }

            state.errorRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = NeuTheme.colors.danger,
                )
            }

            PrimaryButton(
                onClick = onDeploy,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.canDeploy,
                loading = state.isDeploying,
            ) {
                Text(text = stringResource(R.string.deploy))
            }
        }
    }
}

@Composable
private fun BranchCommitInfo(
    branch: Branch?,
    commit: BranchCommit?,
    isLoading: Boolean,
) {
    val colors = NeuTheme.colors
    val typography = NeuTheme.typography
    val spacing = NeuTheme.spacing
    val density = LocalDensity.current
    val messageLineHeight = with(density) { typography.bodyMedium.lineHeight.toDp() }
    val messageTextHeight = with(density) { typography.bodyMedium.fontSize.toDp() }
    val detailsLineHeight = with(density) { typography.bodySmall.lineHeight.toDp() }
    val detailsTextHeight = with(density) { typography.bodySmall.fontSize.toDp() }

    Panel(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(spacing.md)
                .height(messageLineHeight * 2 + spacing.xs + detailsLineHeight),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            when {
                branch == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.select_branch),
                        color = colors.onSurfaceMuted,
                    )
                }

                isLoading -> {
                    Column(modifier = Modifier.height(messageLineHeight * 2)) {
                        listOf(1f, 0.6f).forEach { fraction ->
                            Box(
                                modifier = Modifier.height(messageLineHeight),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Skeleton(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .height(messageTextHeight)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.height(detailsLineHeight),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Skeleton(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .height(detailsTextHeight)
                        )
                    }
                }

                else -> {
                    Text(
                        text = commit?.message ?: branch.name,
                        modifier = Modifier.height(messageLineHeight * 2),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val details = listOfNotNull(
                        (commit?.sha ?: branch.sha)?.take(7),
                        commit?.author,
                        commit?.committedAt?.let {
                            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                .format(Date(it))
                        },
                    )
                    if (details.isNotEmpty()) {
                        Text(
                            text = details.joinToString(" · "),
                            style = NeuTheme.typography.bodySmall,
                            color = colors.onSurfaceMuted,
                        )
                    }
                }
            }
        }
    }
}
