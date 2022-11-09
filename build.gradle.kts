buildscript {
    extra.apply {
        set("kotlin_version", Kotlin.version)
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        mavenLocal()
    }

    dependencies {
        classpath (Build.androidBuildTools)
        classpath (Build.kotlinGradlePlugin)
        classpath (Build.hiltAndroid)
        classpath (Build.navigationSafeArgsPlugin)
        classpath (Build.googleServicesPlugin)
        classpath (Build.firebaseCrashlyicsPlugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("allDeps", DependencyReportTask::class)

tasks.withType<Wrapper> {
    gradleVersion = "7.3.1"
}