package com.karasu256.vcnative.ui.settings.licenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.settings.SettingsScaffold
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LicenseDetailScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun LicensesScreen(
    navigator: DestinationsNavigator,
    viewModel: LicensesViewModel = hiltViewModel(),
) {
    val licenses by viewModel.licenses.collectAsStateWithLifecycle()

    SettingsScaffold(
        title = stringResource(R.string.licenses),
        onBack = { navigator.navigateUp() },
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(licenses, key = { it.id }) { license ->
                ListItem(
                    headlineContent = { Text(text = license.name) },
                    modifier = Modifier.clickable {
                        navigator.navigate(LicenseDetailScreenDestination(licenseId = license.id))
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
}
