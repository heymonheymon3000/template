package com.gm.template.ui

import com.gm.template.plugin.PluginFragment

interface MainActivityInterface {
    fun launchFeature(pluginActionName: String)
    fun loadFragment(pluginFragment: PluginFragment, addToBackStack: Boolean)
    fun loadFragmentByAction(pluginActionName: String,
                             addToBackStack: Boolean,
                             arguments: HashMap<String, Any>)
}