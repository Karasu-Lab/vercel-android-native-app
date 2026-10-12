package com.karasu256.vcnative.ui.components

import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

enum class SwipeDirection {
    LeftToRight,
    RightToLeft,
}

val SwipeDirection.alignment: Alignment.Horizontal
    get() = when (this) {
        SwipeDirection.LeftToRight -> Alignment.Start
        SwipeDirection.RightToLeft -> Alignment.End
    }

/** A swipe action of a list item: which way it is swiped, and what it executes. */
abstract class ListItemSwipeAction(val direction: SwipeDirection) {
    abstract fun execute()
}

@Composable
fun <A : ListItemSwipeAction> SwipeableListItem(
    actions: List<A>,
    background: @Composable (A) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    val leftToRight = actions.firstOrNull { it.direction == SwipeDirection.LeftToRight }
    val rightToLeft = actions.firstOrNull { it.direction == SwipeDirection.RightToLeft }

    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        enableDismissFromStartToEnd = leftToRight != null,
        enableDismissFromEndToStart = rightToLeft != null,
        onDismiss = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> leftToRight?.execute()
                SwipeToDismissBoxValue.EndToStart -> rightToLeft?.execute()
                SwipeToDismissBoxValue.Settled -> Unit
            }
            scope.launch { state.reset() }
        },
        backgroundContent = {
            when (state.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> leftToRight?.let { background(it) }
                SwipeToDismissBoxValue.EndToStart -> rightToLeft?.let { background(it) }
                SwipeToDismissBoxValue.Settled -> Unit
            }
        },
        content = { content() },
    )
}
