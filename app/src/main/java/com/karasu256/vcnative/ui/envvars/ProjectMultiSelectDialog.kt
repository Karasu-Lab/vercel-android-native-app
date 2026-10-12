package com.karasu256.vcnative.ui.envvars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.deployments.create.ProjectIcon
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.PrimaryButton
import com.karasu256.vcnative.ui.theme.SecondaryButton
import com.karasu256.vcnative.ui.theme.Skeleton

@Composable
fun ProjectMultiSelectDialog(
    projects: List<ProjectItem>?,
    selectedIds: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selection by remember { mutableStateOf(selectedIds) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.label_linked_projects)) },
        text = {
            if (projects == null) {
                Column {
                    repeat(4) {
                        Skeleton(
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
                        val checked = project.project.id in selection
                        val toggle = {
                            selection = if (checked) {
                                selection - project.project.id
                            } else {
                                selection + project.project.id
                            }
                        }
                        ListItem(
                            headlineContent = { Text(text = project.project.name) },
                            modifier = Modifier.clickable(onClick = toggle),
                            leadingContent = { ProjectIcon(project = project, size = 32.dp) },
                            trailingContent = {
                                Checkbox(checked = checked, onCheckedChange = null)
                            },
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(NeuTheme.spacing.md),
            ) {
                PrimaryButton(
                    onClick = { onConfirm(selection) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
                SecondaryButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        },
    )
}
