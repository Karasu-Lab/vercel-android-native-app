package com.karasu256.vcnative.ui.deployments.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuSkeleton
import com.karasu256.vcnative.ui.theme.NeuTheme
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Vercel

@Composable
fun ProjectSelectDialog(
    projects: List<ProjectItem>?,
    onSelect: (ProjectItem) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        title = { Text(text = stringResource(R.string.select_project)) },
        text = {
            if (projects == null) {
                Column {
                    repeat(4) {
                        NeuSkeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = NeuTheme.spacing.sm)
                                .height(40.dp)
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(items = projects, key = { it.project.id }) { project ->
                        ListItem(
                            headlineContent = { Text(text = project.project.name) },
                            modifier = Modifier.clickable { onSelect(project) },
                            trailingContent = { ProjectIcon(project = project, size = 32.dp) },
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun ProjectIcon(
    project: ProjectItem,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val avatar = project.avatar
    when {
        avatar != null -> Image(
            bitmap = avatar,
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        project.project.avatarHash != null -> NeuSkeleton(modifier = modifier.size(size), shape = CircleShape)
        else -> Icon(
            imageVector = SimpleIcons.Vercel,
            contentDescription = null,
            modifier = modifier.size(size),
        )
    }
}
