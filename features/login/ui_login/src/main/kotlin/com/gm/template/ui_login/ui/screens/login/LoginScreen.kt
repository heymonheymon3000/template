package com.gm.template.ui_login.ui.screens.login

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gm.template.BuildConfig
import com.gm.template.components.DefaultScreenUI
import com.gm.template.ui_login.ui.LoginEvents
import com.gm.template.ui_login.ui.LoginState

@Composable
fun LoginScreen(
    state: LoginState,
    events: (LoginEvents) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    DefaultScreenUI(
        modifier = Modifier.systemBarsPadding(),
        isStatusBarVisible = true,
        isNavigationBarVisible = true,
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(LoginEvents.OnRemoveHeadFromQueueEvent) },
        progressBarState = state.progressBarState,
        featureProgressBarState = state.progressBarState) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) { padding ->

            Column(modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(

                    text = "Hello Login Module - ${BuildConfig.VERSION_CODE}",
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colors.onPrimary
                    } else {
                        Color(0xFF333333)
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

        }
    }
}