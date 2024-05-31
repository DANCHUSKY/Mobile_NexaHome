package com.example.mvvmlogin.ui.theme.login.ui.AppScreens.UserScreen

import UserTokenHolder.token
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.BottomMenu
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.Screen
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Composable
fun PasswordScreen(navController: NavHostController) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente los elementos
    ) {
        Text(
            text = "Contraseña",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 200.dp) // Agregar espacio en la parte inferior del texto
        )
        PasswordField(
            label = "Contraseña Antigua",
            value = oldPassword,
            onValueChange = { oldPassword = it }
        )
        Spacer(modifier = Modifier.weight(1.0f))

        PasswordField(
            label = "Contraseña Nueva",
            value = newPassword,
            onValueChange = { newPassword = it }
        )
        Spacer(modifier = Modifier.weight(0.1f))
        PasswordField(
            label = "Confirmar Contraseña",
            value = newPassword, // Usar la nueva contraseña para la confirmación
            onValueChange = { newPassword = it }
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Button(
            onClick = {
                // Llamar a la función que maneja la lógica de actualización de contraseña
                updatePassword(oldPassword, newPassword)
            }
        ) {
            Text(text = "Cambiar Contraseña")
        }

        Spacer(modifier = Modifier.weight(1.0f))
        BottomMenu(selectedScreen = Screen.Profile, onScreenSelected = { /* No hacer nada */ }, navController = navController)
    }
}

fun updatePassword(oldPassword: String, newPassword: String) {
    // URL del endpoint con parámetros de consulta
    val url = "https://192.168.184.116:7771/user/changePass?oldPassword=${oldPassword}&newPassword=${newPassword}"

    // Construir la solicitud
    val request = Request.Builder()
        .url(url)
        .header("Authorization", "$token")
        .build()

    // Realizar la solicitud de manera asíncrona con un cliente HTTP inseguro
    val client = getUnsafeOkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            // Manejar la respuesta aquí
            if (response.isSuccessful) {
                println("Contraseña cambiada exitosamente")
            } else {
                println("Error al cambiar la contraseña")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            // Manejar el fallo de la solicitud aquí
            println("Fallo en la solicitud: ${e.message}")
        }
    })
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    var passwordVisibility by remember { mutableStateOf(false) }
    val visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
    val icon = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        trailingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Toggle password visibility"
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.Gray,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.Gray
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
