package com.example.xml_app.navigation

import kotlinx.serialization.Serializable

sealed interface ApiRoute {
    @Serializable
    data object Home : ApiRoute

    @Serializable
    data object Cart : ApiRoute

    @Serializable
    data object Favourite : ApiRoute

    @Serializable
    data object More : ApiRoute

    @Serializable
    data object Search : ApiRoute
}

sealed interface SearchRoute {
    @Serializable
    data object Suggestions : SearchRoute

    @Serializable
    data object Results : SearchRoute
}