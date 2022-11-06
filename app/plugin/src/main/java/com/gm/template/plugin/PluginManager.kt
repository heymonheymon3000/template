package com.gm.template.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import java.lang.ref.WeakReference

class PluginManager private constructor() {
    private val pluginFragmentMap = HashMap<String, PluginFragment>()

    fun getPluginFragmentByName(name: String): PluginFragment? { return pluginFragmentMap[name] }

    fun registerPluginFragmentByName(name: String, pluginFragment: PluginFragment) { pluginFragmentMap[name] = pluginFragment }

    fun findPluginByActionName(actionName: String?): Plugin? {
        @SuppressLint("QueryPermissionsNeeded")
        val resolveInfos = context.get()!!.packageManager.queryIntentServices(
            Intent(actionName),
            PackageManager.GET_META_DATA
        )

        val plugins: MutableList<Plugin> = ArrayList()
        for (resolveInfo in resolveInfos) {
            val plugin = Plugin(resolveInfo!!)
            plugins.add(plugin)
            Log.i("Terry",
                "pluginTitle = ${plugin.pluginTitle}, " +
                        "serviceName = ${plugin.serviceName}, " +
                        "servicePackageName = ${plugin.servicePackageName} .")
        }
        return if (plugins.size > 0) {
            plugins[0]
        } else null
    }

    companion object {
        private lateinit var pluginManager: PluginManager
        private lateinit var context: WeakReference<Context>
        fun getInstance(context: Context): PluginManager {
            Companion.context = WeakReference(context.applicationContext)
            if(!this::pluginManager.isInitialized) {
                pluginManager = PluginManager()
            }
            return pluginManager
        }
    }
}


