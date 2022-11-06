apply {
    from("$rootDir/dynamic-feature-library-build.gradle").from("$rootDir/namespace/ui-login-namespace-build.gradle")
}

dependencies {
    "implementation" (project(Modules.login_domain))
    "implementation" (project(Modules.login_interactors))
}