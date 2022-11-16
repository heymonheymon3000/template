package com.gm.template.ui_home.di

import android.content.Context
import com.gm.template.di.DaggerDependencies
import com.gm.template.ui_home.ui.screens.home.HomeFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [DaggerDependencies::class],
    modules = [
        HomeFeatureModule::class,
        HomeModule::class
    ]
)
interface HomeComponent {
    fun inject(homeFragment: HomeFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(daggerDependencies: DaggerDependencies): Builder
        fun build(): HomeComponent
    }
}