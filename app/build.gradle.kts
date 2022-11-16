android {
    namespace = "com.gm.template"

    lint {
        checkReleaseBuilds = false
        quiet = true
        abortOnError = false
    }

    compileSdk = Android.compileSdk
    buildToolsVersion = Android.buildTools

    defaultConfig {
        applicationId = Android.appId
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = Android.versionCode
        versionName = Android.versionName
//        vectorDrawables.useSupportLibrary = true
//        multiDexEnabled  = true

        // testInstrumentationRunner = "com.gm.csmt.CustomTestRunner"
        //setProperty("archivesBaseName", "csmt-" + defaultConfig.versionName + "-" + defaultConfig.versionCode)
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["is_debug"] = true
            multiDexEnabled = true
            isDebuggable = true
        }

        getByName("release") {
            multiDexEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["is_debug"] = false
            isDebuggable = false
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }

    packagingOptions {
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }

    kapt {
        correctErrorTypes = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("gm.keystore")
            keyAlias = "gm"
            storePassword = "gmnomad"
            keyPassword = "gmnomad"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    setDynamicFeatures(
        mutableSetOf(
            Modules.ui_login,
            Modules.ui_home,
        ))
}

dependencies {
    api (project(Modules.components))
    api (project(Modules.plugin))

    api (Dagger.dagger)
    api (Dagger.dagger_android)

    kapt (Hilt.hilt_compiler)
    api (Hilt.android)
    kapt (Hilt.compiler)

    // Import the BoM for the Firebase platform
    api (platform(Google.firebaseBom))
    api (Google.firebaseCrashlytics)
    api (Google.analytics)
    api (Google.firebase_auth)
    api (Google.firebase_ui_auth)
    api (Google.play_services_auth)

    // Facebook Android SDK (only required for Facebook Login)
    // Used in FacebookLoginActivity.
    api ("com.facebook.android:facebook-login:13.2.0")
    api ("androidx.browser:browser:1.4.0")

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
//    "implementation"(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//
//    "api"(project(Modules.acoustic, configuration = "default"))
//
//    "api"(project(Modules.csmt_app_components_sdk))
//
//    "api"(Accompanist.accompanistSystemuicontroller)
//    "api"(Accompanist.animations)
//    "api"(Accompanist.permissions)
//    "api"(Accompanist.flowLayout)
//
//    "api"(AndroidX.appCompat)
//    "api"(AndroidX.coreKtx)
//    "api"(AndroidX.navigation_dynamic_features)
//    "api"(AndroidX.navigation_fragment)
//    "api"(AndroidX.navigation_ui)
//    "api"(AndroidX.lifecycleVmKtx)
//    "api"(AndroidX.lifecycleRunTimeKtx)
//    "api"(AndroidX.window)
//    "api"(AndroidX.biometric)
//    "api"(AndroidX.webkit)
//    "api"(AndroidX.datastoreCore)
//    "api"(AndroidX.datastore)
//    "api"(AndroidX.camera_core)
//    "api"(AndroidX.camera_camera2)
//    "api"(AndroidX.camera_lifecycle)
//    "api"(AndroidX.camera_view)
//    "api"(AndroidX.camera_video)
//    "api"(AndroidX.camera_extensions)
//    "api"(AndroidX.concurrent_futures)
//
//    "api"(Coil.coilCompose)
//
//    "api"(Compose.activity)
//    "api"(Compose.ui)
//    "api"(Compose.material)
//    "api"(Compose.tooling)
//    "api"(Compose.constraintLayout)
//    "api"(Compose.layout)
//    "api"(Compose.icon)
//    "api"(Compose.hiltNavigation)
//    "api"(Compose.composeAnimation)
//    "api"(Compose.ui_binding)
//    "api"(Compose.navigation)
//    "api"(Compose.runTime)
//    "api"(Compose.liveData)
//
//    "api"(Dagger.dagger)
//    "api"(Dagger.dagger_android)
//
//    "api" (Ffmpeg.ffmpeg_kit_full)
//
//    "api"(Google.firebaseCrashlytics)
//    "api"(Google.material)
//    "api"(Google.gson)
//    "api"(Google.play_services_base)
//    "api"(Google.firebase_messaging)
//    "api"(Google.text_recognition)
//    "api"(Google.barcode_scanning)
//    "api"(Google.gson)
//    "api"(Google.exoPlayer)
//    "api"(Google.guava)
//    // Import the BoM for the Firebase platform
//    "api" (platform(Google.firebaseBom))
//    // Declare the dependencies for the Crashlytics and Analytics libraries
//    // When using the BoM, you don't specify versions in Firebase library dependencies
//

    //    "api"(Dagger.dagger)
//    "api"(Dagger.dagger_android)
//    "kapt" (Hilt.hilt_compiler)
//    "api" (Hilt.android)
//    "kapt" (Hilt.compiler)
//
//    "api" (Kotlin.kotlin_stdlib)
//
//    "api"(Square.retrofit2)
//    "api"(Square.retrofit2_xml){
//        exclude(module = "stax")
//        exclude(module = "stax-api")
//        exclude(module = "xpp3")
//    }
//
//    // debugImplementation because LeakCanary should only run in debug builds.
//    "debugImplementation" (Square.leakcanary)
//
//    // testing dependencies
//    "androidTestImplementation"(AndroidXTest.runner)
//    "androidTestImplementation"(ComposeTest.uiTestJunit4)
//    "debugImplementation"(ComposeTest.uiTestManifest)
//    "androidTestImplementation"(HiltTest.hiltAndroidTesting)
//    "kaptAndroidTest"(Hilt.compiler)
//    "androidTestImplementation"(Junit.junit4)
//
////    testImplementation "androidx.arch.core:core-testing:$versions.coreTesting"
////    testImplementation "junit:junit:$versions.junit"
////    testImplementation "io.mockk:mockk:$versions.mockk"
////    testImplementation "com.google.truth:truth:$versions.truth"
////    testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$versions.coroutines"
////    testImplementation "org.junit.jupiter:junit-jupiter-api:$versions.junitJupiter"
////    testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine:$versions.junitJupiter"
    implementation(kotlin("reflect"))
}

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    kotlin(KotlinPlugins.serialization) version Kotlin.version
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    id("kotlin-android")

}