package com.gm.template.ui_login.plugin

import com.gm.template.plugin.PluginManager
import com.gm.template.plugin.PluginService
import com.gm.template.ui_login.R
import com.gm.template.ui_login.ui.screens.login.LoginFragment

class PluginLogin: PluginService() {
    override fun registerPluginFragment(name: String) {
        val loginFragment = LoginFragment().apply {
            navGraphId = com.gm.template.R.id.login_nav_graph
            pluginId = R.id.loginFragment
        }

        PluginManager.getInstance(applicationContext)
            .registerPluginFragmentByName(
                name  = resources.getString(com.gm.template.R.string.module_login),
                pluginFragment = loginFragment)
    }
}