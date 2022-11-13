package com.gm.template.ui

import com.gm.template.core.domain.UIComponent
import com.gm.template.plugin.PluginFragment

sealed class MainEvents {
    object OnRemoveHeadFromQueueEvent: MainEvents()

    data class OnUpdateFeatureProgressBarEvent(
        val progress: Float
    ): MainEvents()

    data class OnAppendToMessageQueueEvent(
        val uiComponent: UIComponent
    ): MainEvents()

    data class OnLoadFeatureEvent(
        val pluginFragment: PluginFragment): MainEvents()

    data class OnLoadFragmentByActionEvent(
        val pluginActionName: String): MainEvents()
}
