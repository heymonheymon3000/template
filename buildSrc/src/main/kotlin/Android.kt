object Android {
    const val appId = "com.gm.template2"
    const val compileSdk = 32
    const val buildTools = "32.0.0"
    const val minSdk = 27
    const val targetSdk = 32
    val versionCode: Int = ((System.getenv("TFS_BUILD_NUMBER")?.toInt() ?:  253))
    const val versionName = "3.7"
}



