package com.gm.template.ui_login.di

import android.content.Context
import com.gm.template.di.DaggerDependencies
import com.gm.template.ui_login.ui.screens.login.LoginFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [DaggerDependencies::class],
    modules = [
        LoginFeatureModule::class,
        LoginModule::class
    ]
)
interface LoginComponent {
    fun inject(loginFragment: LoginFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(daggerDependencies: DaggerDependencies): Builder
        fun build(): LoginComponent
    }
}