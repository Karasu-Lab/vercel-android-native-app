package com.karasu256.vcnative.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Refreshable {
    fun refresh()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshBox(
    refreshable: Refreshable,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = refreshable::refresh,
        modifier = modifier,
        content = content,
    )
}
