package com.gm.template.ui

import com.gm.template.plugin.PluginFragment

sealed class MainEvents {
    data class OnLoadFeatureEvent(
        val pluginFragment: PluginFragment,
        val addToBackStack: Boolean): MainEvents()

    data class OnLoadFragmentByActionEvent(
        val pluginActionName: String): MainEvents()
}
