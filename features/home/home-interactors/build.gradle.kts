apply {
    from("$rootDir/library-build.gradle")
}

plugins {
    kotlin(KotlinPlugins.serialization) version Kotlin.version
}

dependencies {
    "implementation" (project(Modules.core))
    "implementation" (project(Modules.home_datasource))
    "implementation" (project(Modules.home_domain))

    "implementation" (Kotlinx.coroutinesCore) // need for flows

    "testImplementation" (project(Modules.home_datasource_test))
    "testImplementation" (Junit.junit4)
    "testImplementation" (Ktor.ktorClientMock)
    "testImplementation" (Ktor.clientSerialization)
}