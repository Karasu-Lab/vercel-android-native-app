package com.karasu256.vcnative.ui.deployments.create

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant
import com.karasu256.vcnative.ui.theme.NeuTheme
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github

@Composable
fun GitHubLinkButton(
    isLinked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = NeuTheme.linkButton

    NeuButton(
        onClick = onClick,
        modifier = modifier,
        variant = NeuButtonVariant.Primary,
        contentPadding = style.contentPadding,
        shape = style.shape,
    ) {
        if (!isLinked) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                modifier = Modifier.size(style.iconSize),
            )
        }
        Icon(
            imageVector = SimpleIcons.Github,
            contentDescription = stringResource(if (isLinked) R.string.github_linked else R.string.link_github),
            modifier = Modifier.size(style.iconSize),
        )
    }
}
