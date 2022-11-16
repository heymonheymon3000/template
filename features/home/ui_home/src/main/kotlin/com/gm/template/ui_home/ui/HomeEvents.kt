package com.gm.template.ui_home.ui

import com.gm.template.core.domain.UIComponent

sealed class HomeEvents {
    object OnRemoveHeadFromQueueEvent: HomeEvents()
    data class OnAppendToMessageQueueEvent(
        val uiComponent: UIComponent
    ): HomeEvents()
}