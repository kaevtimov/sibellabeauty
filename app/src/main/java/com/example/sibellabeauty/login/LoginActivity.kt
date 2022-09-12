package com.example.sibellabeauty.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.sibellabeauty.dashboard.DashboardActivity
import com.example.sibellabeauty.R
import com.example.sibellabeauty.SibellaBeautyApplication
import com.example.sibellabeauty.data.FirebaseResponse
import com.example.sibellabeauty.register.RegisterActivity
import com.example.sibellabeauty.theme.AppTheme
import com.example.sibellabeauty.viewModelFactory
import com.example.sibellabeauty.widgets.LogoWithTitle
import kotlinx.coroutines.delay

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModelFactory {
        LoginViewModel((application as SibellaBeautyApplication).usersRepo!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setContent {
            AppTheme() {
                LoginScreen()
            }
        }
    }

    @Composable
    fun LoginScreen() {
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
                LoginInput(loginInputVisibility)
                Spacer(modifier = Modifier.height(142.dp))
                LoginButton(loginInputVisibility)
                Spacer(modifier = Modifier.height(22.dp))
                RegisterButton(loginInputVisibility)
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
    fun LoginInput(visible: Boolean) {
        if (!visible) return

        val username = viewModel.username
        val password = viewModel.password

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
        }
    }

    @Composable
    fun LoginButton(visible: Boolean) {
        if (!visible) return

        val enableBtn = viewModel.enableLoginButton

        Button(
            enabled = enableBtn.value,
            modifier = Modifier.wrapContentWidth().padding(horizontal = 16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            contentPadding = PaddingValues(),
            onClick = if (enableBtn.value) {
                { tryToLogin() }
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
                    contentDescription = "Login",
                    tint = if (enableBtn.value) Color.Red else Color.Gray
                )
                Text(text = "Login", color = if (enableBtn.value) Color.White else Color.Black)
            }
        }
    }

    @Composable
    fun RegisterButton(visible: Boolean) {
        if (!visible) return

        Button(
            modifier = Modifier.wrapContentWidth().padding(horizontal = 16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            contentPadding = PaddingValues(),
            onClick = { navigateRegister() })
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

    private fun tryToLogin() {
        viewModel.tryLogin()
    }

    private fun openHomeScreen() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun navigateRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private enum class TitlePosition {
        TOP,
        MIDDLE
    }

    private fun setObservers() {
        viewModel.logIn.observe(this) {
            if (it is FirebaseResponse.Success) {
                openHomeScreen()
            } else {
                Toast.makeText(this, (it as? FirebaseResponse.Error)?.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    @Composable
    fun getHalfScreenHeight(): Dp {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp.dp / 2
    }

    @Preview
    @Composable
    fun previewScreen() {
        LoginScreen()
    }
}