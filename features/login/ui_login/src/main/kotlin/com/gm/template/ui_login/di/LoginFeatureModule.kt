package com.gm.template.ui_login.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gm.template.di.ViewModelFactory
import com.gm.template.di.ViewModelKey
import com.gm.template.ui_login.ui.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck
import dagger.multibindings.IntoMap

@Module
@DisableInstallInCheck
abstract class LoginFeatureModule {
    @Binds
    @Suppress("unused")
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    @Suppress("unused")
    internal abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel
}