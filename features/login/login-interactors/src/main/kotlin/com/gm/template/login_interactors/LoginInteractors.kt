package com.gm.template.login_interactors
data class LoginInteractors(val loginToApp: LoginToApp) {

    companion object Factory {
        fun build() : LoginInteractors {
            return LoginInteractors(loginToApp = LoginToApp())
        }
    }
}