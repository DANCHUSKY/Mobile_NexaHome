package com.example.mvvmlogin.ui.theme.login.ui.Login.Request

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


fun loginUser(email: String, passw: String, callback: (Boolean, String?) -> Unit) {
    // Configuración del cliente OkHttpClient para confiar en todos los certificados (Solo para propósitos de desarrollo)
    val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOf()
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
    })

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())

    val ssfFactory = sslContext.socketFactory

    val client = OkHttpClient.Builder()
        .sslSocketFactory(ssfFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .build()

    // Crear un objeto JSON con los datos de inicio de sesión
    val jsonObject = JSONObject()
    jsonObject.put("email", email)
    jsonObject.put("passw", passw)

    // Convertir el objeto JSON a una cadena
    val jsonBody = jsonObject.toString()

    // Crear el cuerpo de la solicitud con los datos en formato JSON
    val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

    val url = "https://192.168.184.116:7771/login"

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // Imprimir el error en Logcat
            Log.e("LoginError", "Error en la petición: ${e.message}")
            callback(false, null)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()

            Log.d("LoginResponse", "Respuesta del servidor: $responseBody")

            if (response.isSuccessful) {
                // Analizar la respuesta del servidor
                val jsonResponse = responseBody?.let { JSONObject(it) }
                if (jsonResponse != null) {
                    if (jsonResponse.has("token")) {
                        // El usuario está registrado, continuar con la navegación
                        val token = jsonResponse.getString("token")
                        callback(true, token)
                    } else {
                        // El usuario no está registrado
                        callback(false, null)
                    }
                }
            } else {
                // La solicitud no fue exitosa
                callback(false, null)
            }
        }
    })
}
