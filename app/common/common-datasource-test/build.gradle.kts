apply {
    from("$rootDir/library-build.gradle")
}

plugins {
//    kotlin(KotlinPlugins.serialization) version Kotlin.version
}

dependencies {
    "api" (project(Modules.common_datasource))
    "api" (project(Modules.common_domain))

    "api" (Ktor.ktorClientMock)
    "api" (Ktor.clientSerialization)
}