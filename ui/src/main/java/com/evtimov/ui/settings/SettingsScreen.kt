package com.evtimov.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbGradients
import com.evtimov.ui.widgets.SbSnackBar
import com.evtimov.ui.widgets.SbSnackBarVisuals
import com.evtimov.ui.widgets.TopBar
import com.evtimov.ui.widgets.rememberVbSnackBarState
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit
) {
    BackHandler {
        navigateBack()
    }

    val snackbarState = rememberVbSnackBarState()
    val coroutineScope = rememberCoroutineScope()

    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            navigateToLogin()
            viewModel.onFinishNavigate()
        }
    }

    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let {
            coroutineScope.launch {
                snackbarState.showSnackBar(
                    SbSnackBarVisuals(message = it)
                )
            }
            viewModel.consumeError()
        }
    }

    Content(
        onLogout = navigateToLogin
    )
    SbSnackBar(snackBarHostState = snackbarState)
}

@Composable
private fun Content(
    onLogout: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(LocalSbGradients.current.gradientBackgroundVerticalLight)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = stringResource(id = R.string.settings_title)
            )
            Column(
                Modifier
                    .fillMaxSize().padding(32.dp)
            ) {
                LogoutButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    Button(
        modifier = modifier.wrapContentWidth(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        onClick = { onLogout() }
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF000000))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.logout_label),
                color = Color.White
            )
        }
    }
}