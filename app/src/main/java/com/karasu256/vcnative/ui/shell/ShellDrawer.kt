package com.karasu256.vcnative.ui.shell

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.karasu256.vcnative.ui.components.NeuSurface

@Composable
fun ShellDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            NeuSurface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp),
                shape = RectangleShape,
            ) {}
        },
        content = content,
    )
}
