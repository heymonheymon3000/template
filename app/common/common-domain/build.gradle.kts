apply {
    from("$rootDir/library-build.gradle")
}

dependencies {
    "api" (project(Modules.core))
}