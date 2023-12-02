package com.evtimov.ui.register

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbGradients
import com.evtimov.ui.theme.LocalSbTypography
import com.evtimov.ui.widgets.SbSnackBar
import com.evtimov.ui.widgets.SbSnackBarVisuals
import com.evtimov.ui.widgets.TopBar
import com.evtimov.ui.widgets.rememberVbSnackBarState
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    navigateBack: () -> Unit
) {
    BackHandler {
        navigateBack()
    }
    val viewModel: RegisterViewModel = hiltViewModel()
    val snackbarState = rememberVbSnackBarState()
    val coroutineScope = rememberCoroutineScope()

    val registerState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = registerState.navigateToNext) {
        if (registerState.navigateToNext) {
            onRegister()
            viewModel.onFinishNavigate()
        }
    }
    LaunchedEffect(key1 = registerState.error) {
        registerState.error?.let {
            coroutineScope.launch {
                snackbarState.showSnackBar(
                    SbSnackBarVisuals(message = it)
                )
            }
            viewModel.consumeError()
        }
    }

    Content(
        state = registerState,
        onRegister = { viewModel.tryRegister() },
        onUsername = { viewModel.setUsername(it) },
        onPassword = { viewModel.setPassword(it) },
        onPasswordConfirm = { viewModel.setPasswordConfirm(it) }
    )
    SbSnackBar(
        modifier = Modifier.statusBarsPadding(),
        snackBarHostState = snackbarState
    )
}

@Composable
fun Content(
    state: RegisterUiState,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onPasswordConfirm: (String) -> Unit,
    onRegister: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalSbGradients.current.gradientBackgroundVerticalLight)
    ) {
        val (topBar, input) = createRefs()

        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = stringResource(id = R.string.register_title)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                .constrainAs(input) {
                    top.linkTo(topBar.bottom, margin = 18.dp)
                    bottom.linkTo(parent.bottom)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegisterInput(
                state = state,
                onUsername = onUsername,
                onPassword = onPassword,
                onPasswordConfirm = onPasswordConfirm
            )
            Spacer(modifier = Modifier.height(142.dp))
            RegisterButton(
                enableRegisterButton = state.buttonEnabled,
                onRegister = onRegister
            )
        }
    }
}

@Composable
fun RegisterButton(
    enableRegisterButton: Boolean,
    onRegister: () -> Unit
) {
    Button(
        enabled = enableRegisterButton,
        modifier = Modifier
            .wrapContentWidth(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        onClick = if (enableRegisterButton) {
            { onRegister() }
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
                text = stringResource(id = R.string.register_label),
                color = if (enableRegisterButton) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun RegisterInput(
    state: RegisterUiState,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onPasswordConfirm: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.usernameState.username,
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
            label = { Text(text = stringResource(id = R.string.register_username)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_man_24),
                    contentDescription = "username_icon"
                )
            },
            isError = state.usernameState.isValid.not(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
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
            label = { Text(text = stringResource(id = R.string.register_password)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_vpn_key_24),
                    contentDescription = "password_icon"
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.confirmPassword,
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
            onValueChange = { onPasswordConfirm(it) },
            label = { Text(text = stringResource(id = R.string.register_confirm_password)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_vpn_key_24),
                    contentDescription = "confirm_password_icon"
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )
    }
}
