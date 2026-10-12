package com.karasu256.vcnative.ui.shell

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle

/**
 * Transitions for pages inside the shell. Switching to a tab root only fades, so leaving a
 * drill-down page through the bottom navigation does not slide.
 */
object ShellNavTransitions : NavHostAnimatedDestinationStyle() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300))
    }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (ShellTab.entries.any { it.destination.route == targetState.destination.route }) {
            fadeOut(tween(300))
        } else {
            slideOutHorizontally(tween(300)) { -it / 4 } + fadeOut(tween(300))
        }
    }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(tween(300)) { -it / 4 } + fadeIn(tween(300))
    }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(tween(300)) { it / 4 } + fadeOut(tween(300))
    }
}
