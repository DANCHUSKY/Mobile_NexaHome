package com.example.mvvmlogin.ui.theme.login.ui.Login.Request

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mvvmlogin.ui.theme.login.ui.Login.EmailInput
import com.example.mvvmlogin.ui.theme.login.ui.Login.NameInput
import com.example.mvvmlogin.ui.theme.login.ui.Login.PasswordField
import com.example.mvvmlogin.ui.theme.login.ui.Login.PhoneInput
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


fun registerUser(name: String, email: String, passw: String, telefono: Int, callback: (Boolean) -> Unit) {
    // Crea un cliente OkHttpClient que confíe en todos los certificados
    val unsafeOkHttpClient = getUnsafeOkHttpClient()

    // Construye el JSON manualmente
    val json = """
        {
            "name": "$name",
            "email": "$email",
            "passw": "$passw",
            "phone": "$telefono"
        }
    """.trimIndent()

    // Construye el cuerpo de la solicitud POST con JSON
    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)

    val request = Request.Builder()
        .url("https://192.168.0.23:7770/register")
        .post(body)
        .build()

    unsafeOkHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            // Lógica para manejar fallos en la solicitud
            callback(false)
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            // Lógica para manejar la respuesta del servidor
            callback(response.isSuccessful)
        }
    })
}

// Función para obtener un OkHttpClient que confía en todos los certificados
fun getUnsafeOkHttpClient(): OkHttpClient {
    try {
        val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder().apply {
            sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            hostnameVerifier(HostnameVerifier { _, _ -> true })
        }.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}


@Composable
fun RegistrationForm(name: String, email: String, password: String, phone: Int, onValueChanged: (String, String, String, Int) -> Unit) {
    NameInput(name) { newName -> onValueChanged(newName, email, password, phone) }
    EmailInput(email) { newEmail -> onValueChanged(name, newEmail, password, phone) }
    Spacer(modifier = Modifier.padding(5.dp))
    PasswordField(password) { newPassword -> onValueChanged(name, email, newPassword, phone) }
    Spacer(modifier = Modifier.padding(5.dp))
    PhoneInput(phone) { newPhone -> onValueChanged(name, email, password, newPhone) } // Sin necesidad de convertir a String
    Spacer(modifier = Modifier.padding(5.dp))
}