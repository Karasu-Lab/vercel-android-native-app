package com.karasu256.vcnative.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.compose.rememberNavController
import com.karasu256.vcnative.navigation.NavGraph
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.components.NeuSurfaceStyle
import com.karasu256.vcnative.ui.theme.NeuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            NeuTheme {
                val navController = rememberNavController()
                NeuSurface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    style = NeuSurfaceStyle.Flat,
                ) {
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
