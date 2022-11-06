apply {
    from("$rootDir/android-library-build.gradle").from("$rootDir/namespace/components-namespace-build.gradle")
}

plugins {
    //kotlin(KotlinPlugins.serialization) version Kotlin.version
}

dependencies {

    "api"(project(Modules.common_datasource))
    "api"(project(Modules.common_datasource_test))
    "api"(project(Modules.common_domain))
    "api"(project(Modules.common_interactors))

    "api"(AndroidX.coreKtx)
    "api"(AndroidX.appCompat)
    "api"(AndroidX.constraintlayout)
    "api"(Google.material)
    "api"(AndroidX.navigation_dynamic_features)

    "api"(AndroidX.navigation_fragment)
    "api"(AndroidX.navigation_ui)

    "api"(AndroidX.lifecycleVmKtx)
    "api"(AndroidX.lifecycleRunTimeKtx)


    "api" (Compose.activity)
    "api" (Compose.composeAnimation)
    "api" (Compose.ui)
    "api" (Compose.material)
    "api" (Compose.tooling)
    "api" (Compose.navigation)
    "api" (Compose.icon)
    "api" (Google.material)
    "api" (Compose.layout)
    "api" (Compose.constraintLayout)

    "api" (Coil.coil)


    "api" (Dagger.dagger)
    "api" (Dagger.dagger_android)
    "kapt" (Hilt.hilt_compiler)
    "api" (Hilt.android)
    "kapt" (Hilt.compiler)

}

