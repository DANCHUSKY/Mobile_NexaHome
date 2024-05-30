import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mvvmlogin.R

import com.example.mvvmlogin.ui.theme.login.ui.Login.Request.loginUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object UserTokenHolder {
    var token: String? = null
}

@Composable
fun LoginScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), navController)
    }
}

@Composable
fun Login(modifier: Modifier, navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorCompletaCampos by remember { mutableStateOf(false) }
    var showErrorEmailInvalido by remember { mutableStateOf(false) }
    var showErrorInicioSesion by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        HeaderImage()
        Spacer(modifier = Modifier.padding(16.dp))
        EmailField(email = email, onEmailChange = { email = it })
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordField(password = password, onPasswordChange = { password = it })
        Spacer(modifier = Modifier.padding(16.dp))

        if (showErrorCompletaCampos) {
            Text(
                text = "Por favor, completa todos los campos.",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (showErrorEmailInvalido) {
            Text(
                text = "El correo electrónico no es válido.",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (showErrorInicioSesion) {
            Text(
                text = "Error al iniciar sesión. Verifica tus credenciales.",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LoginButton(onClick = {
            showErrorCompletaCampos = email.isEmpty() || password.isEmpty()
            showErrorEmailInvalido = !isEmailValid(email) && email.isNotEmpty()

            coroutineScope.launch {
               loginUser(email, password) { success, token ->
                    if (success) {
                        showErrorInicioSesion = false
                        // Guardar el token de usuario
                        UserTokenHolder.token = token
                        // Cambiar al hilo principal para la navegación
                        MainScope().launch {
                            navController.navigate("home")
                        }
                    } else {
                        showErrorInicioSesion = true
                    }
                }
           }

        })
        Spacer(modifier = Modifier.padding(8.dp))
        NewAccount(Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.padding(5.dp))
        RegisterButton(onClick = { navController.navigate("register") })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = { onPasswordChange(it) },
        placeholder = { Text(text = "Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFFFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { passwordVisibility = !passwordVisibility }
            ) {
                Icon(
                    imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisibility) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit
) {
    var isValidEmail by remember { mutableStateOf(true) }

    TextField(
        value = email,
        onValueChange = {
            onEmailChange(it)
            isValidEmail = isEmailValid(it)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFFFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFFFFFFF)),
        content = {
            Text(
                text = "Iniciar Sesión",
                color = Color.Black // Cambiar el color del texto a negro
            )
        })
}

@Composable
fun RegisterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFFFFFFF)),
        content = {
            Text(
                text = "Registrarse",
                color = Color.Black // Cambiar el color del texto a negro
            )
        })
}


@Composable
fun NewAccount(modifier: Modifier) {
    Text(
        text = "¿Aún no tienes una cuenta?",
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFA0AFFF)
    )
}

@Composable
fun HeaderImage() {
    Image(painter = painterResource(id = R.drawable.nexahomelogo), contentDescription = "Header")
}

// Función para verificar la validez del correo electrónico
fun isEmailValid(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return email.matches(emailRegex.toRegex())
}
