object Android {
    const val appId = "com.gm.template"
    const val compileSdk = 32
    const val buildTools = "32.0.0"
    const val minSdk = 27
    const val targetSdk = 32
    val versionCode: Int = ((System.getenv("TFS_BUILD_NUMBER")?.toInt() ?:  175))
    const val versionName = "3.7"
}



