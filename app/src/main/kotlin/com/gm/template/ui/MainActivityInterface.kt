package com.gm.template.ui

import com.gm.template.plugin.Plugin
import com.gm.template.plugin.PluginFragment

interface MainActivityInterface {
    fun loadFragment(pluginFragment: PluginFragment, addToBackStack: Boolean)
    fun loadFragmentByAction(pluginActionName: String,
                             addToBackStack: Boolean,
                             arguments: HashMap<String, Any>,
                             upDateColor: (() -> Unit)? = null)
    fun findPluginByActionName(actionName: String): List<Plugin>
}