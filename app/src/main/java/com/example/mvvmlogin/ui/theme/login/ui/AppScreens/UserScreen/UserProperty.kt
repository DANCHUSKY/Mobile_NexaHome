package com.example.mvvmlogin.ui.theme.login.ui.AppScreens.UserScreen

import UserTokenHolder
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.BottomMenu
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@SuppressLint("CoroutineCreationDuringComposition", "BlockingMethodInNonBlockingContext")
@Composable
fun UserProperty(navController: NavHostController) {
    // Declara una variable mutable para almacenar una lista de propiedades.
    var properties by remember { mutableStateOf<List<Property>?>(null) }
    // Declara una variable mutable para indicar si ocurrió un error al cargar los datos.
    var showError by remember { mutableStateOf(false) }
    // Variable mutable para almacenar la propiedad seleccionada.
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    // Obtiene el token del usuario almacenado.
    val token = UserTokenHolder.token

    LaunchedEffect(token) {
        if (token != null) {
            try {
                val url = "https://192.168.184.116:7771/property/myProperty"
                val response = withContext(Dispatchers.IO) {
                    val client = getUnsafeOkHttpClient()
                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .header("Authorization", token)
                        .build()
                    client.newCall(request).execute()
                }
                if (response.isSuccessful) {
                    val responseData = withContext(Dispatchers.IO) {
                        response.body?.string()
                    }
                    val propiedades = parseProperties(responseData.toString())
                    Log.d("UserProperty", "Número de propiedades: ${propiedades.size}")
                    properties = propiedades
                } else {
                    Log.e("UserProperty", "Error en la respuesta: ${response.code}")
                    showError = true
                }
            } catch (e: IOException) {
                Log.e("UserProperty", "Error al cargar las propiedades", e)
                showError = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (properties != null && properties!!.isNotEmpty()) {
                items(properties!!) { property ->
                    PropertyCard(property = property) {
                        selectedProperty = property
                    }
                }
            } else if (showError) {
                item {
                    ErrorCard()
                }
            }
        }

        BottomMenu(
            selectedScreen = Screen.Profile,
            onScreenSelected = { /* No hacer nada */ },
            navController = navController
        )
    }

    // Mostrar el diálogo de detalles de la propiedad si hay una propiedad seleccionada.
    selectedProperty?.let { property ->
        PropertyDetailsDialog(property) {
            selectedProperty = null
        }
    }
}

@Composable
fun PropertyCard(property: Property, onClick: () -> Unit) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val token = UserTokenHolder.token // Asumiendo que UserTokenHolder.token contiene el token

    LaunchedEffect(property.id) {
        if (token != null) {
            try {
                val client = getUnsafeOkHttpClient()
                val url = "https://192.168.184.116:7771/property/iconImg?idProperty=${property.id}"
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", token)
                    .build()

                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                if (response.isSuccessful) {
                    val inputStream = withContext(Dispatchers.IO) { response.body?.byteStream() }
                    val bitmap = withContext(Dispatchers.IO) { BitmapFactory.decodeStream(inputStream) }
                    imageBitmap = bitmap
                } else {
                    Log.e("PropertyCard", "Error en la respuesta de imagen: ${response.code}")
                }
            } catch (e: IOException) {
                Log.e("PropertyCard", "Error al cargar la imagen de la propiedad", e)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.White)
            .clickable { onClick() } // Detectar el clic
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Imagen de la Propiedad",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Precio: $${property.precio}",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${property.metrosCuadrados} m²",
                color = Color.Black,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PropertyDetailsDialog(property: Property, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Detalles de la Propiedad", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column {
                Text(text = "Precio: $${property.precio}" , color = Color.White)
                Text(text = "Metros Cuadrados: ${property.metrosCuadrados} m²", color = Color.White)
                Text(text = "Ciudad: ${property.ciudad}", color = Color.White)
                Text(text = "Provincia: ${property.provincia}", color = Color.White)
                Text(text = "Calle: ${property.calle}", color = Color.White)
                Text(text = "Número: ${property.numero}", color = Color.White)
                Text(text = "Estado: ${property.estado}", color = Color.White)
                Text(text = "Parking: ${if (property.parking) "Sí" else "No"}", color = Color.White)
                Text(text = "Piscina: ${if (property.piscina) "Sí" else "No"}", color = Color.White)
                Text(text = "Tipo de Propiedad: ${property.tipoPropiedad}", color = Color.White)
                Text(text = "Planta: ${property.planta}", color = Color.White)
                Text(text = "Descripción: ${property.descripcion}", color = Color.White)
                Text(text = "Habitación: ${property.habitacion}", color = Color.White)
                Text(text = "Baño: ${property.bano}", color = Color.White)
                Text(text = "Orientación: ${property.orientacion}", color = Color.White)
                Text(text = "Ascensor: ${if (property.ascensor) "Sí" else "No"}", color = Color.White)
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ErrorCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp)
            .background(color = Color.White)
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Error al cargar los datos",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

data class Property(
    val id: Int,
    val metrosCuadrados: Int,
    val ciudad: String,
    val provincia: String,
    val calle: String,
    val numero: Int,
    val precio: Int,
    val estado: String,
    val parking: Boolean,
    val piscina: Boolean,
    val tipoPropiedad: String,
    val planta: Int,
    val descripcion: String,
    val habitacion: Int,
    val bano: Int,
    val orientacion: String,
    val ascensor: Boolean
)

fun parseProperties(response: String): List<Property> {
    val propertiesList = mutableListOf<Property>()
    val jsonArray = JSONArray(response)
    for (i in 0 until jsonArray.length()) {
        val jsonArrayElement = jsonArray.getJSONArray(i)
        val property = Property(
            id = jsonArrayElement.getInt(0),
            metrosCuadrados = jsonArrayElement.getInt(2),
            ciudad = jsonArrayElement.getString(3),
            provincia = jsonArrayElement.getString(4),
            calle = jsonArrayElement.getString(5),
            numero = jsonArrayElement.getInt(6),
            precio = jsonArrayElement.getInt(7),
            estado = jsonArrayElement.getString(8),
            parking = jsonArrayElement.getBoolean(9),
            piscina = jsonArrayElement.getBoolean(10),
            tipoPropiedad = jsonArrayElement.getString(11),
            planta = jsonArrayElement.getInt(12),
            descripcion = jsonArrayElement.getString(13),
            habitacion = jsonArrayElement.getInt(16),
            bano = jsonArrayElement.getInt(17),
            orientacion = jsonArrayElement.getString(14),
            ascensor = jsonArrayElement.getBoolean(15)
        )
        propertiesList.add(property)
    }
    return propertiesList
}

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
