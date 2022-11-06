object Hilt {
    const val hiltVersion = "2.43.2"
    const val android = "com.google.dagger:hilt-android:$hiltVersion"
    const val compiler = "com.google.dagger:hilt-android-compiler:$hiltVersion"

    private const val hilt_compiler_version = "1.0.0"
    const val hilt_compiler =  "androidx.hilt:hilt-compiler:$hilt_compiler_version"
}

object HiltTest {
    const val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:${Hilt.hiltVersion}"
}