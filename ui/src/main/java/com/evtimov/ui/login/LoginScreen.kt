package com.evtimov.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbGradients
import com.evtimov.ui.theme.LocalSbTypography
import com.evtimov.ui.widgets.SbSnackBarVisuals
import com.evtimov.ui.widgets.TopBar
import com.evtimov.ui.widgets.rememberVbSnackBarState
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit,
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val snackbarState = rememberVbSnackBarState()
    val coroutineScope = rememberCoroutineScope()

    val loginState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = loginState.navigateToDashboard) {
        if (loginState.navigateToDashboard) {
            onLoginSuccess()
            viewModel.onFinishNavigate()
        }
    }
    LaunchedEffect(key1 = loginState.error) {
        loginState.error?.let {
            coroutineScope.launch {
                snackbarState.showSnackBar(
                    SbSnackBarVisuals(message = it)
                )
            }
            viewModel.consumeError()
        }
    }
    Content(
        uiState = loginState,
        onUsername = { viewModel.setUsername(it) },
        onPassword = { viewModel.setPassword(it) },
        onLogin = { viewModel.tryLogin() },
        onRegister = onRegister
    )
}

@Composable
fun Content(
    uiState: LoginUiState,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalSbGradients.current.gradientBackgroundVerticalLight)
    ) {
        val (topBar, content) = createRefs()

        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = stringResource(id = R.string.login_title)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                .constrainAs(content) {
                    top.linkTo(topBar.bottom, margin = 18.dp)
                    bottom.linkTo(parent.bottom)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginInput(
                username = uiState.username,
                password = uiState.password,
                onUsername = onUsername,
                onPassword = onPassword
            )
            Spacer(modifier = Modifier.height(142.dp))
            LoginButton(
                buttonEnabledState = uiState.buttonEnabled,
                onLogin = onLogin
            )
            Spacer(modifier = Modifier.height(22.dp))
            RegisterButton(visible = true, onRegister = onRegister)
        }
    }
}

@Composable
fun LoginInput(
    username: String,
    password: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            textStyle = LocalSbTypography.current.bodyLarge.copy(color = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                placeholderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                backgroundColor = Color.White,
                cursorColor = Color.Black,
                focusedBorderColor = Color(0xFFFFEB3B),
                unfocusedBorderColor = Color(0xFFFFEB3B)
            ),
            shape = RoundedCornerShape(32.dp),
            onValueChange = { onUsername(it) },
            label = { Text(text = stringResource(id = R.string.login_username)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_man_24),
                    contentDescription = "username_icon"
                )
            },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            textStyle = LocalSbTypography.current.bodyLarge.copy(color = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                placeholderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                backgroundColor = Color.White,
                cursorColor = Color.Black,
                focusedBorderColor = Color(0xFFFFEB3B),
                unfocusedBorderColor = Color(0xFFFFEB3B)
            ),
            shape = RoundedCornerShape(32.dp),
            onValueChange = { onPassword(it) },
            label = { Text(text = stringResource(id = R.string.login_password)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_vpn_key_24),
                    contentDescription = "password_icon"
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun LoginButton(
    buttonEnabledState: Boolean,
    onLogin: () -> Unit
) {
    Button(
        enabled = buttonEnabledState,
        modifier = Modifier
            .wrapContentWidth(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        onClick = if (buttonEnabledState) {
            { onLogin() }
        } else {
            {}
        })
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF095FF))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login_title),
                color = if (buttonEnabledState) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun RegisterButton(
    visible: Boolean,
    onRegister: () -> Unit
) {
    if (!visible) return

    Button(
        modifier = Modifier
            .wrapContentWidth(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        onClick = { onRegister() })
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF095FF))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login_register_label),
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun previewScreen() {
    LoginScreen(
        onRegister = {},
        onLoginSuccess = {},
    )
}