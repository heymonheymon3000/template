package com.gm.template.plugin

import androidx.fragment.app.Fragment

abstract class PluginFragment: Fragment() {
    protected var title = ""
    var navGraphId = -1
    var pluginId = -1
    var argument = HashMap<String, Any>()
}