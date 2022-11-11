package com.gm.template.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gm.template.plugin.Plugin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    val application: Application,
): ViewModel() {
    val state: MutableState<MainState> = mutableStateOf(MainState())
    var mainActivityInterface: MainActivityInterface? = null

    fun onTriggerEvent(event: MainEvents) {
        viewModelScope.launch {
            try {
                when (event) {
                    is MainEvents.OnLoadFragmentByActionEvent -> {
                        mainActivityInterface?.loadFragmentByAction(
                            event.pluginActionName,
                            event.addToBackStack,
                            event.arguments)
                    }
                }
            } catch (e: Exception) {
                //logger.error("FileReportViewModel", "launchJob: Exception: $e ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun findPluginByActionName(actionName: String): List<Plugin> {
        val resolveInfoList = application.packageManager.queryIntentServices(
            Intent(actionName),
            PackageManager.GET_META_DATA
        )
        val plugins: MutableList<Plugin> = ArrayList()
        if (resolveInfoList.size == 0) {
            return plugins
        }

        for (resolveInfo in resolveInfoList) {
            if (application.packageName.equals(
                    resolveInfo.serviceInfo.processName,
                    ignoreCase = true)) {
                val plugin = Plugin(resolveInfo)
                plugins.add(plugin)
            }
        }

        return plugins
    }
}