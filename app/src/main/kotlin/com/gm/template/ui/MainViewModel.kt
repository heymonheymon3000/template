package com.gm.template.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    val application: Application,
): ViewModel() {
    val state: MutableState<MainState> = mutableStateOf(MainState())

    private var sessionId = 0
    private val splitInstallManager = SplitInstallManagerFactory.create(application)

    private val  _triggerMainEvent = MutableLiveData<MainEvents>()
    val triggerMainEvent: LiveData<MainEvents>
        get() = _triggerMainEvent

    private val serviceConnection = object : ServiceConnection {
        var pluginInterface: IPluginInterface? = null
        var pluginFragment: PluginFragment? = null

        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            Toast.makeText(application, "onServiceConnected called", Toast.LENGTH_SHORT).show()
            pluginInterface = IPluginInterface.Stub.asInterface(binder)
            pluginInterface?.let {
                try {
                    it.registerFragment("some fragment name")
                    Toast.makeText(application, "find PluginFragment", Toast.LENGTH_SHORT).show()
                    pluginFragment = PluginManager.getInstance(application)
                        .getPluginFragmentByName(state.value.actionName)
                    Toast.makeText(application, "find PluginFragment found", Toast.LENGTH_SHORT).show()
                    pluginFragment?.let { plugin ->
                        plugin.argument = state.value.arguments
                        onTriggerEvent(MainEvents.OnLoadFeatureEvent(plugin, state.value.addToBackStack))
                    }
                } catch (e: Exception) {
                    Log.i("E", "Something wrong")
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

    private var listener: SplitInstallStateUpdatedListener =
        SplitInstallStateUpdatedListener { state ->
            Toast.makeText(application, "${state.sessionId()} == $sessionId", Toast.LENGTH_SHORT).show()

            if (state.sessionId() == sessionId) {
                if(state.moduleNames().isNotEmpty()) {
                    val moduleName = state.moduleNames()[0]
                    when (state.status()) {
                        SplitInstallSessionStatus.INSTALLED -> {
                            Toast.makeText(application, "INSTALLED", Toast.LENGTH_SHORT).show()
                            SplitCompat.install(application)
                            launchFeature(moduleName)
                        }

                        SplitInstallSessionStatus.DOWNLOADING -> {
                            Toast.makeText(application, "DOWNLOADING", Toast.LENGTH_SHORT).show()
                        }

                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {}

                        SplitInstallSessionStatus.INSTALLING -> {
                            Toast.makeText(application, "INSTALLING", Toast.LENGTH_SHORT).show()
                        }

                        SplitInstallSessionStatus.FAILED -> {
                            Toast.makeText(application, "FAILED", Toast.LENGTH_SHORT).show()
                        }

                        SplitInstallSessionStatus.CANCELED -> {
                            Toast.makeText(application, "CANCELED", Toast.LENGTH_SHORT).show()
                        }

                        SplitInstallSessionStatus.CANCELING -> {}
                        SplitInstallSessionStatus.DOWNLOADED -> {
                            Toast.makeText(application, "DOWNLOADED", Toast.LENGTH_SHORT).show()
                        }

                        SplitInstallSessionStatus.PENDING -> {
                            Toast.makeText(application, "PENDING", Toast.LENGTH_SHORT).show()
                        }

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
                        Toast.makeText(application, "OnLoadFeatureEvent was called", Toast.LENGTH_SHORT).show()
                        _triggerMainEvent.value =
                            MainEvents.OnLoadFeatureEvent(
                                event.pluginFragment,
                                event.addToBackStack)
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
            if (application.packageName.equals(
                    resolveInfo.serviceInfo.processName,
                    ignoreCase = true)) {
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
        CoroutineScope(Dispatchers.IO).launch {
            val plugins = findPluginByActionName(pluginActionName)
            if (plugins.isNotEmpty()) {
                SplitCompat.install(application)
                val plugin = plugins[0]
                state.value = state.value.copy(actionName = pluginActionName)
                val bindIntent =
                    Intent().apply { setClassName(plugin.servicePackageName, plugin.serviceName) }
                if(application.bindService(bindIntent, serviceConnection,
                        AppCompatActivity.BIND_AUTO_CREATE
                    )) {
                    state.value = state.value.copy(mIsBound = true)
                }
            } else {
                Toast.makeText(application, "Plugin did not load", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestFeatureInstall(pluginActionName: String) {
        Toast.makeText(application, "Requesting storage module install", Toast.LENGTH_SHORT).show()
        val request = SplitInstallRequest.newBuilder()
            .addModule(pluginActionName)
            .build()

        splitInstallManager.startInstall(request)
            .addOnCompleteListener {
                Toast.makeText(application, "Completed requested for module install", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener { id ->
                sessionId = id
                Toast.makeText(application, "Successfully requested for module install $sessionId", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(application, "Error requesting module install", Toast.LENGTH_SHORT).show()
            }
    }
}