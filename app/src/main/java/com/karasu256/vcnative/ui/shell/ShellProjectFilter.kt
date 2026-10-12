package com.karasu256.vcnative.ui.shell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.deployments.create.ProjectIcon
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import com.karasu256.vcnative.ui.theme.NeuTheme
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Vercel

@Composable
fun ShellProjectFilter(
    project: ProjectItem?,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = NeuTheme.spacing

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(NeuTheme.shapes.md)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (project != null) {
                ProjectIcon(project = project, size = 24.dp)
            } else {
                Icon(
                    imageVector = SimpleIcons.Vercel,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
            Text(
                text = project?.project?.name ?: stringResource(R.string.all_projects),
                modifier = Modifier.widthIn(max = 160.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (project != null) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.clear_project_filter),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onClear)
                    .padding(spacing.xs)
                    .size(20.dp),
            )
        }
    }
}
