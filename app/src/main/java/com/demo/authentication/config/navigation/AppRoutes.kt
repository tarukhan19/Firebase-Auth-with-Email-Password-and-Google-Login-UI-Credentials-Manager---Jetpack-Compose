package com.demo.authentication.config.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenRoute {

    @Serializable
    data object Login: ScreenRoute

    @Serializable
    data object Signup: ScreenRoute

    @Serializable
    data object Home : ScreenRoute
}