package com.gm.template.ui_login.di

import com.gm.template.login_interactors.LoginInteractors
import com.gm.template.login_interactors.LoginToApp
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton

@Module
@DisableInstallInCheck
object LoginModule {
    @Provides
    @Singleton
    fun provideLoginToApp(
        interactors: LoginInteractors
    ): LoginToApp {
        return interactors.loginToApp
    }
}