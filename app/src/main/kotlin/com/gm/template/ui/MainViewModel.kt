package com.gm.template.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.IBinder
import android.widget.Toast
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
import com.gm.template.plugin.*
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    val application: Application): ViewModel()
{
    lateinit var navController: NavController
    private var sessionId = 0
    private val splitInstallManager = SplitInstallManagerFactory.create(application)

    val state: MutableState<MainState> = mutableStateOf(MainState())

    private val _triggerMainEvent: MutableStateFlow<Event<MainEvents?>> =
        MutableStateFlow(Event(null))
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
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "INSTALLED $moduleName")
                        }

                        launchFeature(moduleName)
                    }

                    SplitInstallSessionStatus.DOWNLOADING -> {
                        val totalBytes = state.totalBytesToDownload()
                        val progress = state.bytesDownloaded()
                        // Update progress bar.

                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "DOWNLOADING $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "REQUIRES_USER_CONFIRMATION $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.INSTALLING -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "INSTALLING $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.FAILED -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "FAILED $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.CANCELED -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "CANCELED $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.CANCELING -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "CANCELING $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.DOWNLOADED -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "DOWNLOADED $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.PENDING -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "PENDING $moduleName")
                        }
                    }

                    SplitInstallSessionStatus.UNKNOWN -> {
                        CoroutineScope(Main).launch {
                            this@MainViewModel.state.value =
                                this@MainViewModel.state.value.copy(
                                    featureProgressBarStateText = "UNKNOWN $moduleName")
                        }
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

                    is MainEvents.OnLoadFeatureNavGraphEvent -> {
                        _triggerMainEvent.value =
                            Event(MainEvents.OnLoadFeatureNavGraphEvent(event.navGraphId))
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
            delay(5000)

            val applicationInfo: ApplicationInfo =
                application.packageManager.getApplicationInfo(
                    application.packageName,
                    PackageManager.GET_META_DATA)

           val plugin = applicationInfo.metaData.getString(pluginActionName)

            plugin?.let { className ->
                val navGraphId = (Class.forName(className).kotlin.objectInstance as PluginInterface).getNavGraphId()

                withContext(Main) {
                    Toast.makeText(application, "launchFeature $navGraphId", Toast.LENGTH_SHORT).show()
                    onTriggerEvent(MainEvents.OnLoadFeatureNavGraphEvent(navGraphId))
                }
            }
        }
    }

//    private fun launchFeature(pluginActionName: String) {
//        CoroutineScope(IO).launch {
//            SplitCompat.install(application)
//            delay(5000)
//            val plugins = findPluginByActionName(pluginActionName)
//            if (plugins.isNotEmpty()) {
//                val plugin = plugins[0]
//                state.value = state.value.copy(actionName = pluginActionName)
//                val bindIntent = Intent()
//                    .apply { setClassName(plugin.servicePackageName, plugin.serviceName) }
//                if(application.bindService(bindIntent, serviceConnection, AppCompatActivity.BIND_AUTO_CREATE)) {
//                    state.value = state.value.copy(mIsBound = true)
//                }
//
//            } else {
//                Toast.makeText(application, "Plugin did not load", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun requestFeatureInstall(pluginActionName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(pluginActionName)
            .build()

        splitInstallManager.startInstall(request)
            .addOnCompleteListener {}
            .addOnSuccessListener { id ->
                sessionId = id
            }
            .addOnFailureListener { exception ->
                Toast.makeText(application, "Error requesting module install $pluginActionName", Toast.LENGTH_SHORT).show()
                Toast.makeText(application, exception.message, Toast.LENGTH_SHORT).show()
                when ((exception as SplitInstallException).errorCode) {
                    SplitInstallErrorCode.NETWORK_ERROR -> {

                    }

                    SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED -> {
                        checkForActiveDownloads()
                    }

                    SplitInstallErrorCode.ACCESS_DENIED -> {
                        TODO()
                    }

                    SplitInstallErrorCode.API_NOT_AVAILABLE -> {
                        TODO()
                    }

                    SplitInstallErrorCode.APP_NOT_OWNED -> {
                        TODO()
                    }

                    SplitInstallErrorCode.INCOMPATIBLE_WITH_EXISTING_SESSION -> {
                        TODO()
                    }

                    SplitInstallErrorCode.INSUFFICIENT_STORAGE -> {
                        TODO()
                    }

                    SplitInstallErrorCode.INTERNAL_ERROR -> {
                        TODO()
                    }

                    SplitInstallErrorCode.INVALID_REQUEST -> {
                        TODO()
                    }

                    SplitInstallErrorCode.MODULE_UNAVAILABLE -> {
                        TODO()
                    }

                    SplitInstallErrorCode.NO_ERROR -> {
                        TODO()
                    }

                    SplitInstallErrorCode.PLAY_STORE_NOT_FOUND -> {
                        TODO()
                    }

                    SplitInstallErrorCode.SERVICE_DIED -> {
                        TODO()
                    }

                    SplitInstallErrorCode.SESSION_NOT_FOUND -> {
                        TODO()
                    }

                    SplitInstallErrorCode.SPLITCOMPAT_COPY_ERROR -> {
                        TODO()
                    }

                    SplitInstallErrorCode.SPLITCOMPAT_EMULATION_ERROR -> {
                        TODO()
                    }

                    SplitInstallErrorCode.SPLITCOMPAT_VERIFICATION_ERROR -> {
                        TODO()
                    }
                }
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

    private fun checkForActiveDownloads() {
        splitInstallManager
            // Returns a SplitInstallSessionState object for each active session as a List.
            .sessionStates
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Check for active sessions.
                    for (state in task.result) {
                        if (state.status() == SplitInstallSessionStatus.DOWNLOADING) {
                            // Cancel the request, or request a deferred installation.
                            splitInstallManager.cancelInstall(state.sessionId())
                        }
                    }
                }
            }
    }
}