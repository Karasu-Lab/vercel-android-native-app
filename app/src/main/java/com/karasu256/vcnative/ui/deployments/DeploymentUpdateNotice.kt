package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Notice

@Composable
fun DeploymentUpdateNotice(
    visible: Boolean,
    branch: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Notice(
        visible = visible,
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = stringResource(R.string.new_deployment),
            modifier = Modifier.size(20.dp),
            tint = NeuTheme.colors.primary,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.CallSplit,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        if (branch != null) {
            Text(
                text = branch,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
