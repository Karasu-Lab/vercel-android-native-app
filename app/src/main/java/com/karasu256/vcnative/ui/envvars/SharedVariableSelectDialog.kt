package com.karasu256.vcnative.ui.envvars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Skeleton

@Composable
fun SharedVariableSelectDialog(
    variables: List<EnvironmentVariable>?,
    onSelect: (EnvironmentVariable) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        title = { Text(text = stringResource(R.string.label_shared_variable)) },
        text = {
            when {
                variables == null -> Column {
                    repeat(4) {
                        Skeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = NeuTheme.spacing.sm)
                                .height(40.dp)
                        )
                    }
                }

                variables.isEmpty() -> Text(text = stringResource(R.string.no_environment_variables))

                else -> LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(items = variables, key = { it.id }) { variable ->
                        ListItem(
                            headlineContent = { Text(text = variable.key, maxLines = 1) },
                            modifier = Modifier.clickable { onSelect(variable) },
                            supportingContent = if (variable.isSecret) {
                                { Text(text = stringResource(R.string.label_secret)) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        },
    )
}
