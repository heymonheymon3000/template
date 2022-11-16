package com.gm.template.ui_home.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gm.template.di.ViewModelFactory
import com.gm.template.di.ViewModelKey
import com.gm.template.ui_home.ui.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck
import dagger.multibindings.IntoMap

@Module
@DisableInstallInCheck
abstract class HomeFeatureModule {
    @Binds
    @Suppress("unused")
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    @Suppress("unused")
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel
}