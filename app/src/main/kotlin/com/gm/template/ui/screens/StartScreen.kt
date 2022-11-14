package com.gm.template.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gm.template.BuildConfig
import com.gm.template.components.DefaultScreenUI
import com.gm.template.ui.MainEvents
import com.gm.template.ui.MainState

@Composable
fun StartScreen(
    state: MainState,
    events: (MainEvents) -> Unit)
{
    val scaffoldState = rememberScaffoldState()

    DefaultScreenUI(
        modifier = Modifier.systemBarsPadding(),
        isStatusBarVisible = true,
        isNavigationBarVisible = true,
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(MainEvents.OnRemoveHeadFromQueueEvent) },
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
            horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = "Hello Start up Fragment - ${BuildConfig.VERSION_CODE}",
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp))

                Button(colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1E56A0)),
                    modifier = Modifier.width(240.dp).height(44.dp),
                    onClick = {
                        events(MainEvents.OnLoadFeatureByActionEvent(
                            pluginActionName = state.pluginActionName))
                    }
                ) {
                    Text(
                        text = "Login Module - ${BuildConfig.VERSION_CODE}",
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}
