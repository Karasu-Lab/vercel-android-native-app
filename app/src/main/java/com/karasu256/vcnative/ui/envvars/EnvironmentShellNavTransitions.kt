package com.karasu256.vcnative.ui.envvars

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

/** Switching between the tab roots only fades; drilling into a project slides. */
object EnvironmentShellNavTransitions : NavHostAnimatedDestinationStyle() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300))
    }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (EnvironmentShellTab.entries.any { it.destination.route == targetState.destination.route }) {
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
