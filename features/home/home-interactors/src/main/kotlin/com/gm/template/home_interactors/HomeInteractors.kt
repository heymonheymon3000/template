package com.gm.template.home_interactors
data class HomeInteractors(val loginToApp: String) {

    companion object Factory {
        fun build() : HomeInteractors {
            return HomeInteractors(loginToApp = "LoginToApp")
        }
    }
}