apply {
    from("$rootDir/dynamic-feature-library-build.gradle").from("$rootDir/namespace/ui-home-namespace-build.gradle")
}

dependencies {
    "implementation" (project(Modules.home_domain))
    "implementation" (project(Modules.home_interactors))
}