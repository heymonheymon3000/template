package com.gm.template.ui

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
//                        val totalBytes = state.totalBytesToDownload()
//                        val progress = state.bytesDownloaded()
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

    init { splitInstallManager.registerListener(listener) }

    override fun onCleared() {
        splitInstallManager.unregisterListener(listener)
        super.onCleared()
    }

    fun onTriggerEvent(event: MainEvents) {
        viewModelScope.launch {
            try {
                when (event) {
                    is MainEvents.OnLoadFeatureByActionEvent -> {
                        loadFeatureByAction(event.pluginActionName)
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadFeatureByAction(pluginActionName: String ) {
        if(!state.value.processingNavigation) {
            state.value = state.value.copy(processingNavigation = true)
            if (splitInstallManager.installedModules.contains(pluginActionName)) {
                launchFeature(pluginActionName)
            } else {
                requestFeatureInstall(pluginActionName)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun launchFeature(pluginActionName: String) {
        CoroutineScope(IO).launch {
            try {
                SplitCompat.install(application)

                val applicationInfo: ApplicationInfo =
                    application.packageManager.getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)

                val plugin = applicationInfo.metaData.getString(pluginActionName)

                plugin?.let { className ->
                    val navGraphId = (Class.forName(className)?.kotlin?.objectInstance as PluginInterface).getNavGraphId()

                    withContext(Main) {
                        navController.findDestination(navGraphId)?.let {
                            onTriggerEvent(MainEvents.OnLoadFeatureNavGraphEvent(navGraphId))
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
            } finally {
                CoroutineScope(Main).launch {
                    delay(50)
                    state.value = state.value.copy(processingNavigation = false)
                }
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

                    else -> {}
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
            navController.navigate(R.id.feature_progress_bar_fragment, null, navOptions)
        }
    }

    private fun checkForActiveDownloads() {
        splitInstallManager
            .sessionStates
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (state in task.result) {
                        if (state.status() == SplitInstallSessionStatus.DOWNLOADING) {
                            splitInstallManager.cancelInstall(state.sessionId())
                        }
                    }
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
            //logger.logDebug("")
        }
    }
}
