package com.example.sibellabeauty.register

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.Outcome
import com.example.domain.RegisterState
import com.example.sibellabeauty.R
import com.example.sibellabeauty.theme.AppTheme

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val registerState by viewModel.registerState.collectAsStateWithLifecycle()
            when(val state = registerState) {
                is Outcome.Success -> {
                    if (state.data == RegisterState.SUCCESS) {
                        finish()
                    } else {
                        Toast.makeText(this, "Username taken!", Toast.LENGTH_LONG).show()
                    }
                }
                is Outcome.Failure -> {
                    Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
                }
                is Outcome.Loading -> {}
                else -> Unit
            }
            AppTheme {
                RegisterScreen()
            }
        }
    }

    @Composable
    fun RegisterScreen() {
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
                RegisterInput()
                Spacer(modifier = Modifier.height(142.dp))
                RegisterButton()
            }
        }
    }

    @Composable
    fun RegisterButton() {
        val enableBtn = viewModel.enableLoginButton

        Button(
            enabled = enableBtn.value,
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            contentPadding = PaddingValues(),
            onClick = if (enableBtn.value) {
                { tryToRegister() }
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
                                if (enableBtn.value) Color(0xFFF54E56) else Color(0xFFE1E1E2),
                                if (enableBtn.value) Color(0xFFF095FF) else Color(0xFFE1E1E2)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(id = R.drawable.ic_molumen_lips_2),
                    contentDescription = "Register",
                    tint = if (enableBtn.value) Color.Red else Color.Gray
                )
                Text(text = "Register", color = if (enableBtn.value) Color.White else Color.Black)
            }
        }
    }

    @Composable
    fun RegisterInput() {
        val username = viewModel.username
        val password = viewModel.password
        val passwordConfirm = viewModel.passwordConfirm
        val usernameState = viewModel.usernameState

        Column {
            OutlinedTextField(
                value = username.value,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                onValueChange = { viewModel.setUsername(it) },
                label = { Text(text = "Username", color = Color.White) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_man_24),
                        contentDescription = "username_icon"
                    )
                },
                isError = usernameState.value.not(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor =  Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password.value,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                onValueChange = { viewModel.setPassword(it) },
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
                value = passwordConfirm.value,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                onValueChange = { viewModel.setPasswordConfirm(it) },
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

    private fun tryToRegister() {
        viewModel.register()
        finish()
    }
}