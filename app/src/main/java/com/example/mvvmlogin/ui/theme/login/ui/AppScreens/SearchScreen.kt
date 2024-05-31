package com.example.mvvmlogin.ui.theme.login.ui.AppScreens

// Asegúrate de tener la importación correcta para DropdownMenuItem

import UserTokenHolder
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request



suspend fun fetchImage(propertyId: Int, token: String): Bitmap? {
    val client = getUnsafeOkHttpClient()
    val url = "https://192.168.184.116:7771/property/iconImg?idProperty=$propertyId"
    val request = Request.Builder()
        .url(url)
        .header("Authorization", token)
        .build()

    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val inputStream = response.body?.byteStream()
            BitmapFactory.decodeStream(inputStream)
        } else {
            Log.e("fetchImage", "Error en la respuesta de imagen: ${response.code}")
            null
        }
    }
}

@Composable
fun PropertyCard(property: Property, token: String) {
    var imageBitmap by remember { mutableStateOf(property.image) }

    LaunchedEffect(property.id) {
        if (imageBitmap == null) {
            imageBitmap = fetchImage(property.id, token)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text("Property ID: ${property.id}", style = MaterialTheme.typography.h6)
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

        }
    }
}

@Composable
fun SearchScreen(navController: NavHostController) {
    var selectedCity by remember { mutableStateOf("") }
    var properties by remember { mutableStateOf(listOf<Property>()) }
    var isLoading by remember { mutableStateOf(false) }
    val token = UserTokenHolder.token ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        LocationFilter(onCitySelected = { city ->
            selectedCity = city
        })
        Spacer(modifier = Modifier.height(16.dp))
        SearchButton(onClick = {
            isLoading = true
            fetchPropertyByCity(selectedCity, token) { fetchedProperties ->
                properties = fetchedProperties
                isLoading = false
            }
        })

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(properties) { property ->
                    PropertyCard(property, token)
                }
            }
        }
        BottomMenu(
            selectedScreen = Screen.Busqueda,
            onScreenSelected = { /* No hacer nada */ },
            navController = navController
        )
    }
}

fun fetchPropertyByCity(city: String, token: String, onPropertiesFetched: (List<Property>) -> Unit) {
    val url = "https://192.168.184.116:7771/property/getByCity"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = getUnsafeOkHttpClient()
            val request = Request.Builder()
                .url("${url}?city=${city.trim()}")
                .get()
                .addHeader("Authorization", token)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    println("Response data: $responseData") // Debugging line
                    val properties = parseProperties(responseData, token)
                    withContext(Dispatchers.Main) {
                        onPropertiesFetched(properties)
                    }
                } else {
                    println("Response body is null")
                }
            } else {
                println("Failed to fetch property: ${response.code}")
            }
        } catch (e: java.io.IOException) {
            println("IOException: ${e.message}")
        }
    }
}

@Composable
fun LocationFilter(onCitySelected: (String) -> Unit) {
    var selectedCity by remember { mutableStateOf("") }

    val cities = listOf(
        "A Coruña", "Álava", "Albacete", "Alicante", "Almería", "Asturias",
        "Ávila", "Badajoz", "Barcelona", "Burgos", "Cáceres", "Cádiz",
        "Cantabria", "Castellón", "Ciudad Real", "Córdoba", "Cuenca",
        "Girona", "Granada", "Guadalajara", "Guipúzcoa", "Huelva", "Huesca",
        "Illes Balears", "Jaén", "La Rioja", "Las Palmas", "León", "Lleida",
        "Lugo", "Madrid", "Málaga", "Murcia", "Navarra", "Ourense", "Palencia",
        "Pontevedra", "Salamanca", "Santa Cruz de Tenerife", "Segovia", "Sevilla",
        "Soria", "Tarragona", "Teruel", "Toledo", "Valencia", "Valladolid",
        "Vizcaya", "Zamora", "Zaragoza"
    )

    DropdownCityPicker(
        selectedCity = selectedCity,
        onCitySelected = {
            selectedCity = it
            onCitySelected(it)
        },
        cities = cities
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCityPicker(
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    cities: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedCity,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            label = { Text("Ciudad", color = Color.White) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = if (expanded) "Ocultar menú" else "Mostrar menú",
                    Modifier.clickable { expanded = !expanded }
                )
            },
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                ) {
                    Text(city, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SearchButton(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text("Buscar")
    }
}