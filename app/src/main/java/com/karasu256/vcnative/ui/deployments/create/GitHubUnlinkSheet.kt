package com.karasu256.vcnative.ui.deployments.create

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.PrimaryButton
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubUnlinkSheet(
    onUnlink: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.github_linked)) },
            leadingContent = {
                Icon(
                    imageVector = SimpleIcons.Github,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            },
        )
        PrimaryButton(
            onClick = onUnlink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeuTheme.spacing.lg),
        ) {
            Text(text = stringResource(R.string.unlink_github))
        }
    }
}
