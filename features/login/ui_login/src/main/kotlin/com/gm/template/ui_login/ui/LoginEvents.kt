package com.gm.template.ui_login.ui

import com.gm.template.core.domain.UIComponent

sealed class LoginEvents {
    object OnRemoveHeadFromQueueEvent: LoginEvents()
    data class OnAppendToMessageQueueEvent(
        val uiComponent: UIComponent
    ): LoginEvents()
}