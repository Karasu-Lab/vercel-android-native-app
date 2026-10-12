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
import com.ramcosta.composedestinations.generated.destinations.EnvironmentProjectsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProjectEnvironmentVariablesScreenDestination

/** Only drilling into a project (and back) slides. Everything else, including tab switches, fades. */
object EnvironmentShellNavTransitions : NavHostAnimatedDestinationStyle() {
    private fun AnimatedContentTransitionScope<NavBackStackEntry>.isDrillDown(): Boolean {
        val from = initialState.destination.route
        val to = targetState.destination.route
        val projects = EnvironmentProjectsScreenDestination.route
        val variables = ProjectEnvironmentVariablesScreenDestination.route
        return (from == projects && to == variables) || (from == variables && to == projects)
    }

    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (isDrillDown()) {
            slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300))
        } else {
            fadeIn(tween(300))
        }
    }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (isDrillDown()) {
            slideOutHorizontally(tween(300)) { -it / 4 } + fadeOut(tween(300))
        } else {
            fadeOut(tween(300))
        }
    }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (isDrillDown()) {
            slideInHorizontally(tween(300)) { -it / 4 } + fadeIn(tween(300))
        } else {
            fadeIn(tween(300))
        }
    }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (isDrillDown()) {
            slideOutHorizontally(tween(300)) { it / 4 } + fadeOut(tween(300))
        } else {
            fadeOut(tween(300))
        }
    }
}
