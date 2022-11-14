package com.gm.template.ui_login.plugin

import com.gm.template.plugin.PluginInterface
import com.gm.template.R

object LoginPluginInterfaceImpl: PluginInterface {
    override fun getNavGraphId(): Int {
        return R.id.login_nav_graph
    }
}

