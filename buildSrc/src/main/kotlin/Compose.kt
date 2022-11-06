object Compose {
    const val kotlinCompilerExtensionVersion = "1.3.0"

    private const val activityComposeVersion = "1.4.0"
    const val activity = "androidx.activity:activity-ktx:$activityComposeVersion"

    const val composeVersion = "1.2.1"
    const val ui = "androidx.compose.ui:ui:$composeVersion"
    const val material = "androidx.compose.material:material:$composeVersion"
    const val tooling = "androidx.compose.ui:ui-tooling:$composeVersion"
    const val ui_binding = "androidx.compose.ui:ui-viewbinding:$composeVersion"
    const val icon = "androidx.compose.material:material-icons-extended:$composeVersion"
    const val layout = "androidx.compose.foundation:foundation-layout:$composeVersion"
    const val runTime = "androidx.compose.runtime:runtime:$composeVersion"
    const val liveData = "androidx.compose.runtime:runtime-livedata:$composeVersion"

    private const val navigationVersion = "2.4.1"
    const val navigation = "androidx.navigation:navigation-compose:$navigationVersion"

    private const val constraintLayoutComposeVersion = "1.0.0"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout-compose:$constraintLayoutComposeVersion"

    private const val hiltNavigationComposeVersion = "1.0.0"
    const val hiltNavigation = "androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion"
    const val composeAnimation = "androidx.compose.animation:animation-graphics:$composeVersion"
}

    object ComposeTest {
    const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Compose.composeVersion}"
    const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:${Compose.composeVersion}"
}