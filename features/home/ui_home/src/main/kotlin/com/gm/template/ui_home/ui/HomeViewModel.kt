package com.gm.template.ui_home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gm.template.components.util.UIComponentQueueUtil
import com.gm.template.core.domain.Queue
import com.gm.template.core.domain.UIComponent
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel
@Inject constructor() : ViewModel() {
    val state: MutableState<HomeState> = mutableStateOf(HomeState())

    fun onTriggerEvent(event: HomeEvents) {
        viewModelScope.launch {
            try {
                when (event) {
                    is HomeEvents.OnRemoveHeadFromQueueEvent -> {
                        removeHeadMessage()
                    }

                    is HomeEvents.OnAppendToMessageQueueEvent -> {
                        appendToMessageQueue(event.uiComponent)
                    }
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
}