package com.gm.template.ui.screens.feature_progress_bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gm.template.BuildConfig
import com.gm.template.R
import com.gm.template.components.DefaultScreenUI
import com.gm.template.ui.MainEvents
import com.gm.template.ui.MainState

@Composable
fun FeatureProgressBarScreen(
    state: MainState,
    events: (MainEvents) -> Unit) {

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
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = state.featureProgressBarStateText,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp)

                Column(modifier = Modifier.wrapContentSize().padding(8.dp)) {
                    Image(painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "")
                }

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentHeight()
                        .padding(8.dp))

                Button(colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1E56A0)),
                    modifier = Modifier.wrapContentSize().padding(8.dp),
                    onClick = {  events(MainEvents.OnPopStackEvent) }
                ) {
                    Text(
                        text = "Ok",
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}