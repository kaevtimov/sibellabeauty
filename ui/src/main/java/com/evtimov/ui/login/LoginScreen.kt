package com.evtimov.ui.login

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.widgets.LogoWithTitle
import com.example.domain.Outcome
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val context = LocalContext.current

    val loginState by viewModel.logInState.collectAsStateWithLifecycle()
    when (val state = loginState) {
        is Outcome.Loading -> {}
        is Outcome.Success -> onLoginSuccess()
        is Outcome.Failure -> Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        else -> {}
    }
    Content(
        buttonEnabled = viewModel.enableLoginButton.value,
        username = viewModel.username.value,
        password = viewModel.password.value,
        onUsername = { viewModel.setUsername(it) },
        onPassword = { viewModel.setPassword(it) },
        onLogin = { viewModel.tryLogin() },
        onRegister = onRegister
    )
}

@Composable
fun Content(
    username: String,
    password: String,
    buttonEnabled: Boolean,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    var titlePosState by remember {
        mutableStateOf(TitlePosition.MIDDLE)
    }
    var loginInputVisibility by remember {
        mutableStateOf(false)
    }
    val offsetAnimation: Dp by animateDpAsState(
        if (titlePosState == TitlePosition.MIDDLE) getHalfScreenHeight() else 140.dp,
        tween(800)
    )

    ConstraintLayout(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.onBackground
                    )
                )
            )
    ) {
        val (title, input) = createRefs()
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(input.top)
                }
                .fillMaxWidth()
        ) {
            LogoWithTitle(
                modifier = Modifier.absoluteOffset(y = offsetAnimation),
                titleText = "Login"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(input) {
                    top.linkTo(title.bottom, margin = 18.dp)
                    bottom.linkTo(parent.bottom)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginInput(
                visible = loginInputVisibility,
                username = username,
                password = password,
                onUsername = onUsername,
                onPassword = onPassword
            )
            Spacer(modifier = Modifier.height(142.dp))
            LoginButton(
                visible = loginInputVisibility,
                buttonEnabledState = buttonEnabled,
                onLogin = onLogin
            )
            Spacer(modifier = Modifier.height(22.dp))
            RegisterButton(visible = loginInputVisibility, onRegister = onRegister)
        }

        LaunchedEffect(key1 = true) {
            delay(800)
            titlePosState = TitlePosition.TOP
            delay(800)
            loginInputVisibility = true
        }
    }
}

@Composable
fun LoginInput(
    visible: Boolean,
    username: String,
    password: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
) {
    if (!visible) return

    Column {
        OutlinedTextField(
            value = username,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = onUsername,
            label = { Text(text = "Username", color = Color.White) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_man_24),
                    contentDescription = "username_icon"
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = onPassword,
            label = { Text(text = "Password", color = Color.White) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_vpn_key_24),
                    contentDescription = "password_icon"
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White
            ),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun LoginButton(
    visible: Boolean,
    buttonEnabledState: Boolean,
    onLogin: () -> Unit
) {
    if (!visible) return

    Button(
        enabled = buttonEnabledState,
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 16.dp),
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
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            if (buttonEnabledState) Color(0xFFF54E56) else Color(0xFFE1E1E2),
                            if (buttonEnabledState) Color(0xFFF095FF) else Color(0xFFE1E1E2)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                painter = painterResource(id = R.drawable.ic_molumen_lips_2),
                contentDescription = "Login",
                tint = if (buttonEnabledState) Color.Red else Color.Gray
            )
            Text(text = "Login", color = if (buttonEnabledState) Color.White else Color.Black)
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
            .wrapContentWidth()
            .padding(horizontal = 16.dp),
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
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFF54E56),
                            Color(0xFFF095FF)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                painter = painterResource(id = R.drawable.ic_molumen_lips_2),
                contentDescription = "Register",
                tint = Color.Red
            )
            Text(text = "Register")
        }
    }
}

private enum class TitlePosition {
    TOP,
    MIDDLE
}

@Composable
fun getHalfScreenHeight(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.dp / 2
}

@Preview
@Composable
fun previewScreen() {
    LoginScreen(
        onRegister = {},
        onLoginSuccess = {}
    )
}