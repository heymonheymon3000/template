apply {
    from("$rootDir/library-build.gradle")
}

plugins {
//    kotlin(KotlinPlugins.serialization) version Kotlin.version
//    id(SqlDelight.plugin)
}

dependencies {
    "api" (project(Modules.common_domain))

    "api" (Ktor.core)
    "api" (Ktor.clientSerialization)
    "api" (Ktor.android)

//    "implementation"(SqlDelight.runtime)
}

//sqldelight {
//    database("HeroDatabase") {
//        packageName = "com.codingwithmitch.hero_datasource.cache"
//        sourceFolders = listOf("sqldelight")
//    }
//}