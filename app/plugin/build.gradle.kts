apply {
    from("$rootDir/android-library-build.gradle").from("$rootDir/namespace/plugin-namespace-build.gradle")
}

plugins {
    //kotlin(KotlinPlugins.serialization) version Kotlin.version
}

dependencies {
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
}
