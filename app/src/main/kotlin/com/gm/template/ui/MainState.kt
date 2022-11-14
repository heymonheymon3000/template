package com.gm.template.ui

import com.gm.template.core.domain.ProgressBarState
import com.gm.template.core.domain.Queue
import com.gm.template.core.domain.UIComponent

data class MainState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val featureProgressBarState: ProgressBarState = ProgressBarState.Idle,
    val featureProgressBarStateText: String = "",
    var errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val mIsBound: Boolean = false,
    val arguments: HashMap<String, Any> = HashMap(),
    val actionName: String = "ui_login",
    val pluginActionName: String = "ui_login",
    val addToBackStack: Boolean = false,
    val processingNavigation: Boolean = false
)