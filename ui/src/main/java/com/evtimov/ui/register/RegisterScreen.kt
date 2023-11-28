package com.evtimov.ui.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.example.domain.Outcome
import com.example.domain.RegisterState

@Composable
fun RegisterScreen(
    onRegister: () -> Unit
) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val context = LocalContext.current

    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    when(val state = registerState) {
        is Outcome.Success -> {
            if (state.data == RegisterState.SUCCESS) {
                onRegister()
            } else {
                Toast.makeText(context, "Username taken!", Toast.LENGTH_LONG).show()
            }
        }
        is Outcome.Failure -> {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
        is Outcome.Loading -> {}
        else -> Unit
    }
    Content(
        usernameState = viewModel.usernameState.value,
        enableRegisterButton = viewModel.enableLoginButton.value,
        onRegister = { viewModel.register() },
        username = viewModel.username.value,
        password = viewModel.password.value,
        confirmPassword = viewModel.passwordConfirm.value,
        onUsername = { viewModel.setUsername(it) },
        onPassword = { viewModel.setPassword(it) },
        onPasswordConfirm = { viewModel.setPasswordConfirm(it) }
    )
}

@Composable
fun Content(
    usernameState: Boolean,
    username: String,
    password: String,
    confirmPassword: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onPasswordConfirm: (String) -> Unit,
    enableRegisterButton: Boolean,
    onRegister: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
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
            Text(
                text = "Register",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(input) {
                    top.linkTo(title.bottom, margin = 18.dp)
                    bottom.linkTo(parent.bottom)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegisterInput(
                usernameState = usernameState,
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                onUsername = onUsername,
                onPassword = onPassword,
                onPasswordConfirm = onPasswordConfirm
            )
            Spacer(modifier = Modifier.height(142.dp))
            RegisterButton(
                enableRegisterButton = enableRegisterButton,
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
            .wrapContentWidth()
            .padding(horizontal = 16.dp),
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
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            if (enableRegisterButton) Color(0xFFF54E56) else Color(0xFFE1E1E2),
                            if (enableRegisterButton) Color(0xFFF095FF) else Color(0xFFE1E1E2)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                painter = painterResource(id = R.drawable.ic_molumen_lips_2),
                contentDescription = "Register",
                tint = if (enableRegisterButton) Color.Red else Color.Gray
            )
            Text(text = "Register", color = if (enableRegisterButton) Color.White else Color.Black)
        }
    }
}

@Composable
fun RegisterInput(
    usernameState: Boolean,
    username: String,
    password: String,
    confirmPassword: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onPasswordConfirm: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = username,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = { onUsername(it) },
            label = { Text(text = "Username", color = Color.White) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_man_24),
                    contentDescription = "username_icon"
                )
            },
            isError = usernameState.not(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor =  Color.White
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = { onPassword(it) },
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
                cursorColor =  Color.White
            ),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = confirmPassword,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = { onPasswordConfirm(it) },
            label = { Text(text = "Confirm Password", color = Color.White) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_vpn_key_24),
                    contentDescription = "password_icon"
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor =  Color.White
            ),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}
