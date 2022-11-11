package com.gm.template.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(): ViewModel() {

    var mIsBound = false
    var mArguments: HashMap<String, Any> = HashMap()
    var actionName: String = ""

    val state: MutableState<MainState> = mutableStateOf(MainState())

    fun onTriggerEvent(event: MainEvents) {
        viewModelScope.launch {
            try {
                when (event) {
                    is MainEvents.OnUpdateAvailableEvent -> {
                        state.value = state.value.copy(
                            isLoginFeatureAvailable = event.isAvailable)
                    }

                    else -> {

                    }
                }
            } catch (e: Exception) {
                //logger.error("FileReportViewModel", "launchJob: Exception: $e ${e.cause}")
                e.printStackTrace()
            }
        }
    }
}