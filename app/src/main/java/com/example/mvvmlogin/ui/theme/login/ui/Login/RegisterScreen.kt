package com.example.mvvmlogin.ui.theme.login.ui.Login

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mvvmlogin.R
import com.example.mvvmlogin.ui.theme.login.ui.Login.Request.RegistrationForm
import com.example.mvvmlogin.ui.theme.login.ui.Login.Request.registerUser
import isEmailValid
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Composable
fun RegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(0) } // Cambiado a Int
    var showErrorCamposVacios by remember { mutableStateOf(false) }
    var showErrorEmailInvalido by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            HeaderImage()
            RegistrationForm(name, email, password, phone) { newName, newEmail, newPassword, newPhone ->
                name = newName
                email = newEmail
                password = newPassword
                phone = newPhone // Se asigna directamente el nuevo valor de teléfono
            }

            if (showErrorCamposVacios) {
                Text(
                    text = "Por favor, completa todos los campos.",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            if (showErrorEmailInvalido) {
                Text(
                    text = "El correo electrónico no es válido.",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            RegisterButton2(onClick = {
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phone != 0) { // Verifica que el teléfono no sea 0
                    showErrorCamposVacios = false
                    if (isEmailValid(email)) {
                        showErrorEmailInvalido = false
                        registerUser(name, email, password, phone) { success ->
                            if (success) {
                                MainScope().launch {
                                    navController.navigate("login")
                                }
                            } else {
                                MainScope().launch {
                                    Toast.makeText(context, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        showErrorEmailInvalido = true
                    }
                } else {
                    showErrorCamposVacios = true
                }
            })

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInput(email: String, onValueChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onValueChanged(it) },
        label = { Text("Correo Electrónico") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFFFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput(name: String, onValueChanged: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = { onValueChanged(it) },
        label = { Text("Nombre") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFFFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInput(phone: Int, onValueChanged: (Int) -> Unit) {
    TextField(
        value = phone.toString(), // Convertir a String
        onValueChange = { onValueChanged(it.toIntOrNull() ?: 0) }, // Convertir a Int
        label = { Text("Teléfono") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFFFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(password: String, onPasswordChange: (String) -> Unit) {
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


@Composable
fun RegisterButton2(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFFFFFFF)),
        content = {
            Text(
                text = "Registrarse",
                color = Color.Black
            )
        }
    )
}

@Composable
fun HeaderImage() {
    // Imagen del encabezado
    Image(
        painter = painterResource(id = R.drawable.nexahomelogo),
        contentDescription = "Header",
        modifier = Modifier
            .fillMaxWidth()
    )
}