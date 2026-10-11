package com.karasu256.vcnative.ui.splash

sealed interface SplashEvent {
    data object NavigateMain : SplashEvent

    data object NavigateHome : SplashEvent
}
