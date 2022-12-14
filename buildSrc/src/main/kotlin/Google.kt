object Google {
    private const val materialVersion = "1.4.0"
    const val material = "com.google.android.material:material:$materialVersion"

    private const val exoPlayerVersion = "2.16.0"
    const val exoPlayer = "com.google.android.exoplayer:exoplayer:$exoPlayerVersion"

    private const val gsonVersion = "2.8.9"
    const val gson = "com.google.code.gson:gson:$gsonVersion"

    private const val firebaseBomVersion = "31.0.3"
    const val firebaseBom = "com.google.firebase:firebase-bom:$firebaseBomVersion"
    // no need to specify version when using the Bom above
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val analytics =  "com.google.firebase:firebase-analytics-ktx"


    // Google Identity Services SDK (only required for Auth with Google)
    const val play_services_auth  = "com.google.android.gms:play-services-auth:20.3.0"

    // Firebase UI
    // Used in FirebaseUIActivity
    // Firebase Authentication (Kotlin)
    const val firebase_auth  = "com.google.firebase:firebase-auth-ktx"
    const val firebase_ui_auth  = "com.firebaseui:firebase-ui-auth:8.0.2"

    const val googleServicesPluginVersion = "4.3.14"
    const val firebaseCrashlyticsPluginVersion = "2.8.1"

    private const val text_recognitionVersion = "16.0.0-beta1"
    const val text_recognition = "com.google.mlkit:text-recognition:$text_recognitionVersion"

    private const val barcode_scanningVersion = "17.0.0"
    const val barcode_scanning = "com.google.mlkit:barcode-scanning:$barcode_scanningVersion"

    private const val play_core_version = "1.10.3"
    const val play_core = "com.google.android.play:core:$play_core_version"

    private const val guava_version = "31.1-android"
    const val guava = "com.google.guava:guava:$guava_version"

    private const val play_services_version = "18.0.0"
    const val play_services_base = "com.google.android.gms:play-services-base:$play_services_version"
    const val firebase_messaging = "com.google.firebase:firebase-messaging:$play_services_version"



}