package com.gm.template.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gm.template.core.util.Event
import com.gm.template.plugin.IPluginInterface
import com.gm.template.plugin.Plugin
import com.gm.template.plugin.PluginFragment
import com.gm.template.plugin.PluginManager
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    val application: Application): ViewModel()
{
    private var sessionId = 0
    private val splitInstallManager = SplitInstallManagerFactory.create(application)

    val state: MutableState<MainState> = mutableStateOf(MainState())

    private val _triggerMainEvent: MutableStateFlow<Event<MainEvents?>> = MutableStateFlow(Event(null))
    val triggerMainEvent: StateFlow<Event<MainEvents?>> get() = _triggerMainEvent

    private val serviceConnection = object : ServiceConnection {
        var pluginInterface: IPluginInterface? = null
        var pluginFragment: PluginFragment? = null

        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            pluginInterface = IPluginInterface.Stub.asInterface(binder)
            pluginInterface?.let {
                try {
                    it.registerFragment("some fragment name")
                    pluginFragment = PluginManager.getInstance(application)
                        .getPluginFragmentByName(state.value.actionName)
                    pluginFragment?.let { plugin ->
                        plugin.argument = state.value.arguments
                        onTriggerEvent(MainEvents.OnLoadFeatureEvent(plugin, state.value.addToBackStack))
                    }
                } catch (e: Exception) {
                    Toast.makeText(application, "Something wrong", Toast.LENGTH_SHORT).show()
                } finally {
                    if (state.value.mIsBound) {
                        state.value = state.value.copy(mIsBound = false)
                        application.unbindService(this)
                    }
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            pluginInterface = null
            state.value = state.value.copy(mIsBound = false)
        }
    }

    private var listener: SplitInstallStateUpdatedListener = SplitInstallStateUpdatedListener { state ->
            if (state.sessionId() == sessionId) {
                if(state.moduleNames().isNotEmpty()) {
                    val moduleName = state.moduleNames()[0]
                    when (state.status()) {
                        SplitInstallSessionStatus.INSTALLED -> {
                            Toast.makeText(application, "INSTALLED", Toast.LENGTH_SHORT).show()
                            launchFeature(moduleName)
                        }

                        SplitInstallSessionStatus.DOWNLOADING -> {}
                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {}
                        SplitInstallSessionStatus.INSTALLING -> {}
                        SplitInstallSessionStatus.FAILED -> {}
                        SplitInstallSessionStatus.CANCELED -> {}
                        SplitInstallSessionStatus.CANCELING -> {}
                        SplitInstallSessionStatus.DOWNLOADED -> {}
                        SplitInstallSessionStatus.PENDING -> {}
                        SplitInstallSessionStatus.UNKNOWN -> {}

                    }
                }
            }
        }

    init {
        splitInstallManager.registerListener(listener)
    }

    override fun onCleared() {
        splitInstallManager.unregisterListener(listener)
        super.onCleared()
    }

    fun onTriggerEvent(event: MainEvents) {
        viewModelScope.launch {
            try {
                when (event) {
                    is MainEvents.OnLoadFragmentByActionEvent -> {
                        loadFragmentByAction(event.pluginActionName)
                    }

                    is MainEvents.OnLoadFeatureEvent -> {
                        _triggerMainEvent.value =
                            Event(MainEvents.OnLoadFeatureEvent(event.pluginFragment, event.addToBackStack))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun findPluginByActionName(actionName: String): List<Plugin> {
        val resolveInfoList = application.packageManager.queryIntentServices(
            Intent(actionName),
            PackageManager.GET_META_DATA
        )
        val plugins: MutableList<Plugin> = ArrayList()
        if (resolveInfoList.size == 0) {
            return plugins
        }

        for (resolveInfo in resolveInfoList) {
            if (application.packageName.equals(resolveInfo.serviceInfo.processName, ignoreCase = true)) {
                val plugin = Plugin(resolveInfo)
                plugins.add(plugin)
            }
        }

        return plugins
    }

    private fun loadFragmentByAction(pluginActionName: String ) {
        if (splitInstallManager.installedModules.contains(pluginActionName)) {
            launchFeature(pluginActionName)
        } else {
            requestFeatureInstall(pluginActionName)
        }
    }

    private fun launchFeature(pluginActionName: String) {
        SplitCompat.install(application)
        val plugins = findPluginByActionName(pluginActionName)
        if (plugins.isNotEmpty()) {
            val plugin = plugins[0]
            state.value = state.value.copy(actionName = pluginActionName)
            val bindIntent = Intent()
                .apply { setClassName(plugin.servicePackageName, plugin.serviceName) }
            if(application.bindService(bindIntent, serviceConnection, AppCompatActivity.BIND_AUTO_CREATE)) {
                state.value = state.value.copy(mIsBound = true)
            }
        } else {
            Toast.makeText(application, "Plugin did not load", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestFeatureInstall(pluginActionName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(pluginActionName)
            .build()

        splitInstallManager.startInstall(request)
            .addOnCompleteListener {
            }
            .addOnSuccessListener { id ->
                sessionId = id
            }
            .addOnFailureListener {
                Toast.makeText(application, "Error requesting module install", Toast.LENGTH_SHORT).show()
            }
    }
}