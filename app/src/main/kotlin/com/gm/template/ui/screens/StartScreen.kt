package com.gm.template.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gm.template.BuildConfig
import com.gm.template.ui.MainEvents
import com.gm.template.ui.MainState

@Composable
fun StartScreen(
    state: MainState,
    events: (MainEvents) -> Unit)
{
    Column(modifier = Modifier.fillMaxWidth(),
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
                events(MainEvents.OnLoadFragmentByActionEvent(
                    pluginActionName = state.pluginActionName,
                    addToBackStack = state.addToBackStack,
                    arguments = state.arguments))
            }
        ) {
            Text(
                text = "Login Module - ${BuildConfig.VERSION_CODE}",
                style = MaterialTheme.typography.button
            )
        }
    }
}