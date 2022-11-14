object Android {
    const val appId = "com.gm.template2"
    const val compileSdk = 33
    const val buildTools = "33.0.0"
    const val minSdk = 27
    const val targetSdk = 32
    val versionCode: Int = ((System.getenv("TFS_BUILD_NUMBER")?.toInt() ?:  384))
    const val versionName = "3.7"
}



