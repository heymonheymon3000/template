package com.gm.template.core.domain

sealed class FilterOrder {

    object Ascending: FilterOrder()

    object Descending: FilterOrder()
}