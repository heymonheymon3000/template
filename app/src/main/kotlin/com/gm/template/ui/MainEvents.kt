package com.gm.template.ui

sealed class MainEvents {
    data class OnLoadFragmentByActionEvent(
        val pluginActionName: String,
        val addToBackStack: Boolean,
        val arguments: HashMap<String, Any>): MainEvents()
}
