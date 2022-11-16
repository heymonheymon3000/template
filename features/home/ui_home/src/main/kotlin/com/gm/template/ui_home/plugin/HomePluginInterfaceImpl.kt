package com.gm.template.ui_home.plugin

import com.gm.template.plugin.PluginInterface
import com.gm.template.R

object HomePluginInterfaceImpl: PluginInterface {
    override fun getNavGraphId(): Int {
        return R.id.home_nav_graph
    }
}

