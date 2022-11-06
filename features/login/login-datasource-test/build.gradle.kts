apply {
    from("$rootDir/library-build.gradle")
}

plugins {
    kotlin(KotlinPlugins.serialization) version Kotlin.version
}

dependencies {
    "implementation"(project(Modules.login_datasource))
    "implementation"(project(Modules.login_domain))

    "implementation"(Ktor.ktorClientMock)
    "implementation"(Ktor.clientSerialization)
}