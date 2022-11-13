package com.gm.template.core.domain

sealed class UIComponent{

    data class Dialog(
        val id: String,
        val title: String,
        val description: String,
    ): UIComponent()

    data class SnackBar(
        val id: String,
        val title: String,
        val description: String,
    ): UIComponent()

    data class None(
        val message: String,
    ): UIComponent()
}