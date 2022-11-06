package com.gm.template.ui_login.plugin

import android.util.Log
import com.gm.template.plugin.PluginManager
import com.gm.template.plugin.PluginService
import com.gm.template.ui_login.R
import com.gm.template.ui_login.ui.screens.login.LoginFragment

class PluginLogin: PluginService() {
    private val TAG = PluginLogin::class.java.simpleName

    override fun registerPluginFragment(name: String) {
        Log.i("Terry", "registerPluginFragment: PluginLogin")
        Log.i("Terry", "navGraphId = ${com.gm.template.R.id.login_nav_graph}")
        Log.i("Terry", "pluginId = ${R.id.loginFragment}")

        val loginFragment = LoginFragment().apply {
            navGraphId = com.gm.template.R.id.login_nav_graph
            pluginId = R.id.loginFragment
        }

        PluginManager.getInstance(applicationContext).registerPluginFragmentByName(
            "login", loginFragment)
    }
}