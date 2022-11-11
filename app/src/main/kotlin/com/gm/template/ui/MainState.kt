package com.gm.template.ui

import com.gm.template.core.domain.ProgressBarState

data class MainState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val isLoginFeatureAvailable: Boolean = false
)