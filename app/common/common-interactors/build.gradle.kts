apply {
    from("$rootDir/library-build.gradle")
}

plugins {
//    kotlin(KotlinPlugins.serialization) version Kotlin.version
}

dependencies {
    "api" (project(Modules.core))
    "api" (project(Modules.common_datasource))
    "api" (project(Modules.common_domain))

    "api" (Kotlinx.coroutinesCore) // need for flows

    "testImplementation" (project(Modules.common_datasource_test))
    "testImplementation" (Junit.junit4)
    "testImplementation" (Ktor.ktorClientMock)
    "testImplementation" (Ktor.clientSerialization)
}