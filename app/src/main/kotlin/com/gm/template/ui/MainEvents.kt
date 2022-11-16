package com.gm.template.ui

import com.gm.template.core.domain.UIComponent

sealed class MainEvents {
    object OnRemoveHeadFromQueueEvent: MainEvents()
    object OnPopStackEvent: MainEvents()
    data class OnAppendToMessageQueueEvent(
        val uiComponent: UIComponent
    ): MainEvents()

    data class OnLoadFeatureNavGraphEvent(
        val navGraphId: Int): MainEvents()

    data class OnLoadFeatureByActionEvent(
        val pluginActionName: String): MainEvents()
}
