package com.karasu256.vcnative.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalFocusManager

fun Modifier.unfocus(): Modifier = this then UnfocusElement

private data object UnfocusElement : ModifierNodeElement<UnfocusNode>() {
    override fun create() = UnfocusNode()
    override fun update(node: UnfocusNode) = Unit
}

private class UnfocusNode : DelegatingNode(), CompositionLocalConsumerModifierNode {
    init {
        delegate(
            SuspendingPointerInputModifierNode {
                detectTapGestures { currentValueOf(LocalFocusManager).clearFocus() }
            }
        )
    }
}
