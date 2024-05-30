package com.example.mvvmlogin.ui.theme.login.ui.AppScreens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
fun HomeScreen(navController: NavHostController) {
    var properties by remember { mutableStateOf<List<Property>?>(null) }
    var showError by remember { mutableStateOf(false) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val token = UserTokenHolder.token

    LaunchedEffect(token) {
        if (token != null) {
            try {
                val url = "https://192.168.0.23:7770/property/allProperty"
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
                    val propiedades = parseProperties(responseData.toString(), token)
                    Log.d("HomeScreen", "Número de propiedades: ${propiedades.size}")
                    properties = propiedades
                } else {
                    Log.e("HomeScreen", "Error en la respuesta: ${response.code}")
                    showError = true
                }
            } catch (e: IOException) {
                Log.e("HomeScreen", "Error al cargar las propiedades", e)
                showError = true
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Cargando...", color = Color.White, fontSize = 16.sp)
                }
            }
        } else {
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
        }

        AddButton() {
            navController.navigate("property")
        }

        BottomMenu(
            selectedScreen = Screen.Home,
            onScreenSelected = { /* No hacer nada */ },
            navController = navController
        )
    }

    selectedProperty?.let { property ->
        PropertyDetailsDialog(property) {
            selectedProperty = null
        }
    }
}

@Composable
fun PropertyCard(property: Property, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.White)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            property.image?.let {
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
                Text(text = "Precio: $${property.precio}", color = Color.White)
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
    val email: String,
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
    val ascensor: Boolean,
    val image: Bitmap? // Añadido para almacenar la imagen cargada
)

suspend fun parseProperties(response: String, token: String): List<Property> {
    val propertiesList = mutableListOf<Property>()
    val jsonArray = JSONArray(response)
    for (i in 0 until jsonArray.length()) {
        val jsonArrayElement = jsonArray.getJSONArray(i)
        val property = Property(
            id = jsonArrayElement.getInt(0),
            email = jsonArrayElement.getString(1),
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
            ascensor = jsonArrayElement.getBoolean(15),
            image = loadImage(jsonArrayElement.getInt(0), token) // Cargar la imagen durante el análisis
        )
        propertiesList.add(property)
    }
    return propertiesList
}

suspend fun loadImage(propertyId: Int, token: String): Bitmap? {
    return try {
        val client = getUnsafeOkHttpClient()
        val url = "https://192.168.0.23:7770/property/iconImg?idProperty=$propertyId"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", token)
            .build()

        val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
        if (response.isSuccessful) {
            val inputStream = withContext(Dispatchers.IO) { response.body?.byteStream() }
            withContext(Dispatchers.IO) { BitmapFactory.decodeStream(inputStream) }
        } else {
            Log.e("PropertyCard", "Error en la respuesta de imagen: ${response.code}")
            null
        }
    } catch (e: IOException) {
        Log.e("PropertyCard", "Error al cargar la imagen de la propiedad", e)
        null
    }
}

@Composable
fun AddButton(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 40.dp, bottom = 30.dp)
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.background(color = Color.Transparent)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

enum class Screen {
    Home,
    Busqueda,
    Profile,
}

@Composable
fun BottomMenu(selectedScreen: Screen, onScreenSelected: (Screen) -> Unit, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF000000)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomMenuItem(
            icon = Icons.Default.Home,
            isSelected = selectedScreen == Screen.Home
        ) { navController.navigate("home") }
        BottomMenuItem(
            icon = Icons.Default.Search,
            isSelected = selectedScreen == Screen.Busqueda
        ) { navController.navigate("search") }
        BottomMenuItem(
            icon = Icons.Default.AccountCircle,
            isSelected = selectedScreen == Screen.Profile
        ) { navController.navigate("user") }
    }
}

@Composable
fun BottomMenuItem(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val iconTint = if (isSelected) Color.White else Color.LightGray

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier
            .padding(vertical = 15.dp, horizontal = 13.dp)
            .clickable {
                if (!isSelected) {
                    onClick()
                }
            }
    )
}
