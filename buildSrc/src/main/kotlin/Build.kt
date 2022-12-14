object Build {
    private const val androidBuildToolsVersion = "7.3.1"
    const val androidBuildTools = "com.android.tools.build:gradle:$androidBuildToolsVersion"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"

    const val hiltAndroid = "com.google.dagger:hilt-android-gradle-plugin:${Hilt.hiltVersion}"

    const val navigationSafeArgsPlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${AndroidX.navigation_version}"

    const val googleServicesPlugin = "com.google.gms:google-services:${Google.googleServicesPluginVersion}"

    const val firebaseCrashlyicsPlugin = "com.google.firebase:firebase-crashlytics-gradle:${Google.firebaseCrashlyticsPluginVersion}"

}