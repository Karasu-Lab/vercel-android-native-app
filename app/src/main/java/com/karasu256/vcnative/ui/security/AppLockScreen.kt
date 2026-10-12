package com.karasu256.vcnative.ui.security

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.PrimaryButton

@Composable
fun AppLockScreen(
    onUnlocked: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as FragmentActivity
    val title = stringResource(R.string.biometric_prompt_title)
    val negativeText = stringResource(R.string.cancel)
    val currentOnUnlocked by rememberUpdatedState(onUnlocked)
    val authenticate = {
        BiometricAuthenticator.authenticate(activity, title, negativeText) { success ->
            if (success) currentOnUnlocked()
        }
    }

    LaunchedEffect(Unit) {
        if (BiometricAuthenticator.isAvailable(context)) {
            authenticate()
        } else {
            // The enrolled biometrics were removed; stay usable instead of locking the user out.
            currentOnUnlocked()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NeuTheme.spacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(NeuTheme.spacing.lg))
        Text(
            text = stringResource(R.string.app_locked),
            style = NeuTheme.typography.title,
        )
        Spacer(Modifier.height(NeuTheme.spacing.xl))
        PrimaryButton(onClick = { authenticate() }) {
            Text(text = stringResource(R.string.unlock))
        }
    }
}
