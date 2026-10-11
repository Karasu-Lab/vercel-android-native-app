package com.karasu256.vcnative.ui.activitys

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.karasu256.vcnative.data.repository.GitHubAuthRepository
import com.karasu256.vcnative.data.model.AppTheme
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuSurfaceStyle
import com.karasu256.vcnative.ui.theme.NeuNavTransitions
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appThemeViewModel: AppThemeViewModel by viewModels()

    @Inject
    lateinit var gitHubAuthRepository: GitHubAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleAuthCallback(intent)
        setContent {
            val appTheme by appThemeViewModel.theme.collectAsStateWithLifecycle()
            val darkTheme = when (appTheme) {
                AppTheme.System -> isSystemInDarkTheme()
                AppTheme.Light -> false
                AppTheme.Dark -> true
            }

            LaunchedEffect(darkTheme) {
                val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }

            NeuTheme(darkTheme = darkTheme) {
                NeuSurface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    style = NeuSurfaceStyle.Flat,
                ) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        defaultTransitions = NeuNavTransitions,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthCallback(intent)
    }

    private fun handleAuthCallback(intent: Intent?) {
        val uri = intent?.data ?: return
        lifecycleScope.launch { gitHubAuthRepository.handleCallback(uri) }
    }
}
