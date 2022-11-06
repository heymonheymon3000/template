package com.gm.template.core.domain

sealed class ProgressBarState{

    object Loading: ProgressBarState()

    object Idle: ProgressBarState()
}