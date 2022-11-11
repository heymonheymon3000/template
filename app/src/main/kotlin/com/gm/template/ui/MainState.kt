package com.gm.template.ui

import com.gm.template.core.domain.ProgressBarState

data class MainState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val mIsBound: Boolean = false,
    val arguments: HashMap<String, Any> = HashMap(),
    val actionName: String = "ui_login",
    val pluginActionName: String = "ui_login",
    val addToBackStack: Boolean = false,)