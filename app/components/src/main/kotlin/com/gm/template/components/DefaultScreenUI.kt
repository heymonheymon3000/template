package com.gm.template.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.gm.template.core.domain.ProgressBarState
import com.gm.template.core.domain.Queue
import com.gm.template.core.domain.UIComponent
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * @param queue: Dialogs
 * @param content: The content of the UI.
 */
@Composable
fun DefaultScreenUI(
    modifier: Modifier = Modifier,
    isStatusBarVisible: Boolean = true,
    isNavigationBarVisible: Boolean = true,
    navigationBarColor: Color = Color.White,
    statusBarColor: Color = Color.Blue,
    queue: Queue<UIComponent> = Queue(mutableListOf()),
    onRemoveHeadFromQueue: () -> Unit,
    progressBarState: ProgressBarState = ProgressBarState.Idle,
    featureProgressBarState: ProgressBarState = ProgressBarState.Idle,
    content: @Composable () -> Unit,
){
    val systemUiController: SystemUiController = rememberSystemUiController()
    systemUiController.isStatusBarVisible = isStatusBarVisible
    systemUiController.isNavigationBarVisible = isNavigationBarVisible

    val scaffoldState = rememberScaffoldState()
    SideEffect {
        if(isStatusBarVisible) {
            systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = false)
        }
        if(isNavigationBarVisible) {
            systemUiController.setNavigationBarColor(color = navigationBarColor, darkIcons = true)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ){ padding ->
        Box(
            modifier = Modifier.padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ){
            content()

            // process the queue
            if(!queue.isEmpty()){
                queue.peek()?.let { uiComponent ->
                    if(uiComponent is UIComponent.Dialog){
                        GenericDialog(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            title = uiComponent.title,
                            description = uiComponent.description,
                            onRemoveHeadFromQueue = onRemoveHeadFromQueue
                        )
                    }
                }
            }

            if(progressBarState is ProgressBarState.Loading){
                CircularIndeterminateProgressBar()
            }

            if(featureProgressBarState is ProgressBarState.Loading){
                CircularIndeterminateProgressBar()
            }
        }
    }
}
