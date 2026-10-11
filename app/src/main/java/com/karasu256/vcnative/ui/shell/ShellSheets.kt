package com.karasu256.vcnative.ui.shell

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.AppTheme
import com.karasu256.vcnative.data.model.VercelUser
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuText
import com.karasu256.vcnative.ui.theme.NeuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSheet(
    user: VercelUser?,
    avatar: ImageBitmap?,
    onLogout: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val spacing = NeuTheme.spacing

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        ListItem(
            headlineContent = { Text(text = user?.name ?: user?.username.orEmpty()) },
            supportingContent = user?.email?.let { email -> { Text(text = email) } },
            leadingContent = { UserAvatar(avatar = avatar, size = 40.dp) },
        )
        NeuButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.lg),
        ) {
            NeuText(text = stringResource(R.string.logout))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSheet(
    current: AppTheme,
    onSelect: (AppTheme) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        AppTheme.entries.forEach { theme ->
            ListItem(
                headlineContent = { Text(text = stringResource(theme.labelRes)) },
                modifier = Modifier.clickable { onSelect(theme) },
                leadingContent = { Icon(imageVector = theme.icon, contentDescription = null) },
                trailingContent = if (theme == current) {
                    { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
                } else {
                    null
                },
            )
        }
    }
}

private val AppTheme.icon: ImageVector
    get() = when (this) {
        AppTheme.System -> Icons.Default.BrightnessAuto
        AppTheme.Light -> Icons.Default.LightMode
        AppTheme.Dark -> Icons.Default.DarkMode
    }

@get:StringRes
private val AppTheme.labelRes: Int
    get() = when (this) {
        AppTheme.System -> R.string.theme_system
        AppTheme.Light -> R.string.theme_light
        AppTheme.Dark -> R.string.theme_dark
    }
