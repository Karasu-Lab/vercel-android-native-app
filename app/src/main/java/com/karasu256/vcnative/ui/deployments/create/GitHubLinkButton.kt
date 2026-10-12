package com.karasu256.vcnative.ui.deployments.create

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.PillButton
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github

@Composable
fun GitHubLinkButton(
    isLinked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PillButton(
        onClick = onClick,
        icons = if (isLinked) listOf(SimpleIcons.Github) else listOf(Icons.Default.Link, SimpleIcons.Github),
        contentDescription = stringResource(if (isLinked) R.string.github_linked else R.string.link_github),
        modifier = modifier,
    )
}
