package com.gm.template.ui_home.ui

import com.gm.template.core.domain.ProgressBarState
import com.gm.template.core.domain.Queue
import com.gm.template.core.domain.UIComponent

data class HomeState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val featureProgressBarState: ProgressBarState = ProgressBarState.Idle,
    val featureProgressBarStateText: String = "",
    var errorQueue: Queue<UIComponent> = Queue(mutableListOf()))