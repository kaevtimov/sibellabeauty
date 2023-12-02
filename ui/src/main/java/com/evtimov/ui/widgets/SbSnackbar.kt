package com.evtimov.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.evtimov.ui.R

@Composable
fun SbSnackBar(
    modifier: Modifier = Modifier,
    snackBarHostState: SbSnackBarHostState
) {
    SnackbarHost(
        hostState = snackBarHostState.state,
        modifier = modifier.fillMaxWidth(),
        snackbar = {
            val data = it.visuals as SbSnackBarVisuals
            CustomSnackBar(
                isError = data.isError,
                textMessage = data.message,
            )
        }
    )
}

@Composable
private fun CustomSnackBar(
    modifier: Modifier = Modifier,
    isError: Boolean,
    textMessage: String
) {
    val additionalPadding = 18.dp
    val imageVector = if (isError) {
        R.drawable.ic_error
    } else {
        R.drawable.ic_success
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .customShadow(
                offsetX = 0.dp,
                offsetY = 10.dp,
                shadowBlurRadius = 45.dp,
                alpha = 0.2f,
                cornersRadius = 0.dp
            ),
        backgroundColor = Color.White,
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = imageVector),
                    contentDescription = null
                )
                Column(modifier = Modifier.padding(start = additionalPadding)) {
                    Text(
                        text = textMessage,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

class SbSnackBarHostState(internal val state: SnackbarHostState) {

    suspend fun showSnackBar(visuals: SbSnackBarVisuals) {
        state.showSnackbar(visuals)
    }
}

@Composable
fun rememberVbSnackBarState(): SbSnackBarHostState {
    return remember {
        val state = SnackbarHostState()
        SbSnackBarHostState(state)
    }
}

data class SbSnackBarVisuals(
    val title: String? = null,
    override val message: String,
    val isError: Boolean = true,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false
) : SnackbarVisuals