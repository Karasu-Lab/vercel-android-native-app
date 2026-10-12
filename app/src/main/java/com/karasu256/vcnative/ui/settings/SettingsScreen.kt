package com.karasu256.vcnative.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.shell.RootNavigator
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.karasu256.vcnative.ui.theme.SectionHeader
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.AccountScreenDestination
import com.ramcosta.composedestinations.generated.destinations.DataManagementScreenDestination
import com.ramcosta.composedestinations.generated.destinations.DebugSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LicensesScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SecurityScreenDestination

@Destination<ShellGraph>
@Composable
fun SettingsScreen(
    rootNavigator: RootNavigator,
) {
    val navigator = rootNavigator.navigator

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        SectionHeader(title = stringResource(R.string.settings_section_general))
        Column {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.account)) },
                modifier = Modifier.clickable { navigator.navigate(AccountScreenDestination) },
                leadingContent = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
            )
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.security)) },
                modifier = Modifier.clickable { navigator.navigate(SecurityScreenDestination) },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null)
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
            )
        }
        SectionHeader(title = stringResource(R.string.settings_section_other))
        Column {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.data_management)) },
                modifier = Modifier.clickable { navigator.navigate(DataManagementScreenDestination) },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Storage, contentDescription = null)
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
            )
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.debug_settings)) },
                modifier = Modifier.clickable { navigator.navigate(DebugSettingsScreenDestination) },
                leadingContent = {
                    Icon(imageVector = Icons.Default.BugReport, contentDescription = null)
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
            )
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.licenses)) },
                modifier = Modifier.clickable { navigator.navigate(LicensesScreenDestination) },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}
