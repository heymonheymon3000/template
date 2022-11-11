package com.gm.template.ui

sealed class MainEvents {
    data class OnUpdateAvailableEvent(
        val isAvailable: Boolean): MainEvents()
}