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
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.gm.template.R
import com.gm.template.components.util.UIComponentQueueUtil
import com.gm.template.core.domain.Queue
import com.gm.template.core.domain.UIComponent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainViewModel
@Inject constructor(
    val application: Application): ViewModel()
{
    lateinit var navController: NavController
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
                        onTriggerEvent(MainEvents.OnLoadFeatureEvent(plugin))
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
                            this@MainViewModel.state.value = this@MainViewModel.state.value.copy(
                                featureProgressBarStateText = "Installed $moduleName")

                            launchFeature(moduleName)
                        }

                        SplitInstallSessionStatus.DOWNLOADING -> {
//                                val progress =
//                                    (((state.bytesDownloaded().toDouble() * 100 / state.totalBytesToDownload()).toFloat())/100).toFloat()

                                this@MainViewModel.state.value = this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "Downloading $moduleName")
                        }

                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {

                        }

                        SplitInstallSessionStatus.INSTALLING -> {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "INSTALLING $moduleName")
                        }

                        SplitInstallSessionStatus.FAILED -> {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "FAILED $moduleName")
                            //navController.popBackStack()
                        }

                        SplitInstallSessionStatus.CANCELED -> {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "CANCELED $moduleName")
                            //navController.popBackStack()
                        }

                        SplitInstallSessionStatus.CANCELING -> {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "CANCELING $moduleName")
                            //navController.popBackStack()
                        }

                        SplitInstallSessionStatus.DOWNLOADED -> {

                        }

                        SplitInstallSessionStatus.PENDING -> {

                        }

                        SplitInstallSessionStatus.UNKNOWN -> {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "UNKNOWN $moduleName")
                            //navController.popBackStack()
                        }
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
                            Event(MainEvents.OnLoadFeatureEvent(event.pluginFragment))
                    }

                    is MainEvents.OnRemoveHeadFromQueueEvent -> {
                        removeHeadMessage()
                    }

                    is MainEvents.OnAppendToMessageQueueEvent -> {
                        appendToMessageQueue(event.uiComponent)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun appendToMessageQueue(uiComponent: UIComponent){
        val queue = state.value.errorQueue
        if(!UIComponentQueueUtil().doesUIComponentAlreadyExistInQueue(queue, uiComponent)) {
            queue.add(uiComponent)
            state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
            state.value = state.value.copy(errorQueue = queue)
        }
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove() // can throw exception if empty
            state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception){
            //logger.logDebug("PRAViewModel","Nothing to remove from DialogQueue")
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
        CoroutineScope(IO).launch {
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
    }

    private fun requestFeatureInstall(pluginActionName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(pluginActionName)
            .build()

        splitInstallManager.startInstall(request)
            .addOnCompleteListener {}
            .addOnSuccessListener { id ->
                sessionId = id
            }
            .addOnFailureListener {
                Toast.makeText(application, "Error requesting module install $pluginActionName", Toast.LENGTH_SHORT).show()
                Toast.makeText(application, it.message, Toast.LENGTH_SHORT).show()
            }

        var navOptions: NavOptions? = null
        navController.currentDestination?.id?.let {
            navOptions = NavOptions.Builder()
                .setPopUpTo(it, inclusive = false, saveState = false)
                .setEnterAnim(R.anim.fadein)
                .setExitAnim(R.anim.fadeout)
                .setPopEnterAnim(R.anim.fadein)
                .setPopExitAnim(R.anim.fadeout)
                .build()
        }

        CoroutineScope(Main).launch {
            navController.navigate(R.id.feature_progress_bar_fragment,
                null,
                navOptions)
        }
    }
}