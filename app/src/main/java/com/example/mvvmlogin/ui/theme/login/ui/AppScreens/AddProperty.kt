package com.example.mvvmlogin.ui.theme.login.ui.AppScreens

import UserTokenHolder.token
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Composable
fun AddProperty(navController: NavHostController, token: String) {


    // Estados de los campos del formulario
    var metrosCuadrados by remember { mutableStateOf(0) }
    var ciudad by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf(0) }
    var precio by remember { mutableStateOf(0) } // Cambiado a Int para coincidir con el backend
    var estado by remember { mutableStateOf("") }
    var parking by remember { mutableStateOf(false) }
    var piscina by remember { mutableStateOf(false) }
    var tipoPropiedad by remember { mutableStateOf("") }
    var planta by remember { mutableStateOf(0) }
    var descripcion by remember { mutableStateOf("") }
    var habitacion by remember { mutableStateOf("") }
    var bano by remember { mutableStateOf("") }
    var orientacion by remember { mutableStateOf("") }
    var ascensor by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NexaHome",
            color = Color(0xFFFFCEAD),
            fontSize = 28.sp,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .padding(top = 32.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "UBICACIÓN",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8CEFF),
                    fontSize = 18.sp
                )

                PropertyTextField("Ciudad", ciudad, { ciudad = it }, Icons.Default.LocationCity, keyboardType = KeyboardType.Text)
                PropertyTextField("Provincia", provincia, { provincia = it }, Icons.Default.LocationCity, keyboardType = KeyboardType.Text)
                PropertyTextField("Calle", calle, { calle = it }, Icons.Default.Place, keyboardType = KeyboardType.Text)
                PropertyTextField("Número", numero.toString(), { numero = it.toIntOrNull() ?: 0 }, Icons.Default.Filter1, keyboardType = KeyboardType.Number)

                Spacer(modifier = Modifier.padding(25.dp))

                Text(
                    text = "CARACTERÍSTICAS DE LA PROPIEDAD",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8CEFF),
                    fontSize = 18.sp
                )
                PropertyTextField("Precio", precio.toString(), { precio = it.toIntOrNull() ?: 0 }, Icons.Default.AttachMoney, keyboardType = KeyboardType.Number)
                PropertyTextField("Metros cuadrados", metrosCuadrados.toString(), { metrosCuadrados = it.toIntOrNull() ?: 0 }, Icons.Default.CropSquare, keyboardType = KeyboardType.Number)
                PropertyTextField("Planta", planta.toString(), { planta = it.toIntOrNull() ?: 0 }, Icons.Default.AddLocation, keyboardType = KeyboardType.Number)
                PropertyTextField("Descripción", descripcion, { descripcion = it }, Icons.Default.Description)

                Spacer(modifier = Modifier.padding(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tipo de propiedad: ", color = Color.White, modifier = Modifier.weight(1f))

                    RadioButton(
                        selected = tipoPropiedad == "PISO",
                        onClick = { tipoPropiedad = "PISO" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFB1C8F7), // Color de fondo cuando está seleccionado
                            unselectedColor = Color.White // Color de fondo cuando no está seleccionado
                        ),
                        modifier = Modifier
                            .size(40.dp) // Tamaño del círculo del botón de radio
                            .padding(end = 8.dp) // Espaciado entre el botón y el texto
                    )
                    Text("PISO", color = Color.White)

                    Spacer(modifier = Modifier.width(16.dp)) // Espaciado adicional entre los botones de radio

                    RadioButton(
                        selected = tipoPropiedad == "CASA",
                        onClick = { tipoPropiedad = "CASA" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFB1C8F7), // Color de fondo cuando está seleccionado
                            unselectedColor = Color.White // Color de fondo cuando no está seleccionado
                        ),
                        modifier = Modifier
                            .size(40.dp) // Tamaño del círculo del botón de radio
                            .padding(end = 8.dp) // Espaciado entre el botón y el texto
                    )
                    Text("CASA", color = Color.White)
                }

                Spacer(modifier = Modifier.padding(10.dp))

                CounterField(
                    label = "Habitaciones: ",
                    value = habitacion.toIntOrNull() ?: 0,
                    onValueChange = { newValue -> habitacion = newValue.toString() },
                    decrementButtonColor = Color(0xFFF8CEFF),
                    incrementButtonColor = Color(0xFFF8CEFF),
                    textColor = Color.White
                )

                Spacer(modifier = Modifier.padding(10.dp))
                CounterField(
                    label = "Baños:            ",
                    value = bano.toIntOrNull() ?: 0,
                    onValueChange = { newValue -> bano = newValue.toString() },
                    incrementButtonColor = Color(0xFFF8CEFF),
                    textColor = Color.White
                )

                Spacer(modifier = Modifier.padding(10.dp))

                // DropdownMenu para el campo de Estado
                PropertyDropdown(
                    label = "Estado",
                    selectedItem = estado,
                    onItemSelected = { estado = it },
                    items = listOf("OBRA NUEVA", "BUEN ESTADO", "A REFORMAR")
                )
                PropertyDropdown(
                    label = "Orientación",
                    selectedItem = orientacion,
                    onItemSelected = { orientacion = it },
                    items = listOf("NORTE", "SUR", "ESTE", "OESTE")
                )

                Spacer(modifier = Modifier.padding(25.dp))
                Text(
                    text = "EXTRAS",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8CEFF),
                    fontSize = 18.sp
                )
                PropertyCheckbox(
                    label = "Ascensor",
                    checked = ascensor,
                    onCheckedChange = { ascensor = it },
                    checkboxColor = Color(0xFFBED0FF) // Color personalizado para el checkbox
                )
                PropertyCheckbox(
                    label = "Parking",
                    checked = parking,
                    onCheckedChange = { parking = it },
                    checkboxColor = Color(0xFFBED0FF) // Color personalizado para el checkbox
                )
                PropertyCheckbox(
                    label = "Piscina",
                    checked = piscina,
                    onCheckedChange = { piscina = it },
                    checkboxColor = Color(0xFFBED0FF) // Color personalizado para el checkbox
                )


                Spacer(modifier = Modifier.padding(25.dp))


                ImagePickerDialog { uris ->
                    selectedImages = uris
                }
                AddPropertyButton(
                    onClick = {
                        val propertyUrl = "https://192.168.0.23:7770/property"
                        val imageUploadUrl = "https://192.168.0.23:7770/property/uploadimage/" // Agrega aquí el idProperty real

                        try {
                            val client = getUnsafeOkHttpClient()
                            val jsonObject = JSONObject().apply {
                                put("metrosCuadrados", metrosCuadrados)
                                put("ciudad", ciudad)
                                put("provincia", provincia)
                                put("calle", calle)
                                put("numero", numero)
                                put("precio", precio)
                                put("estado", estado)
                                put("parking", parking)
                                put("piscina", piscina)
                                put("tipoPropiedad", tipoPropiedad)
                                put("planta", planta)
                                put("descripcion", descripcion)
                                put("habitacion", habitacion)
                                put("bano", bano)
                                put("orientacion", orientacion)
                                put("ascensor", ascensor)
                            }

                            val requestBody = jsonObject.toString()
                                .toRequestBody("application/json".toMediaType())

                            val propertyRequest = Request.Builder()
                                .url(propertyUrl)
                                .addHeader("Authorization", token)
                                .post(requestBody)
                                .build()

                            client.newCall(propertyRequest).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    println("La solicitud POST de datos de propiedad falló: ${e.message}")
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    if (response.isSuccessful) {
                                        println("La solicitud POST de datos de propiedad se realizó con éxito.")

                                        if (selectedImages.isNotEmpty()) {

                                            val responseData = response.body?.string()
                                            val jsonResponse = JSONObject(responseData)
                                            val imageFormBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                                            selectedImages.forEach { path ->
                                                val file = File(path)
                                                val requestBody = file.asRequestBody("image/*".toMediaType())
                                                imageFormBuilder.addFormDataPart("files", file.name, requestBody)
                                            }
                                            val imageRequestBody = imageFormBuilder.build()

                                            val imageRequest = Request.Builder()
                                                .url(imageUploadUrl+"?idProperty=${jsonResponse.getInt("Id")}")
                                                .addHeader("Authorization", token)
                                                .post(imageRequestBody)
                                                .build()

                                            client.newCall(imageRequest).enqueue(object : Callback {
                                                override fun onFailure(call: Call, e: IOException) {
                                                    println("La solicitud POST de imágenes seleccionadas falló: ${e.message}")
                                                }

                                                override fun onResponse(call: Call, response: Response) {
                                                    if (response.isSuccessful) {
                                                        println("La solicitud POST de imágenes seleccionadas se realizó con éxito.")
                                                        MainScope().launch {
                                                            navController.navigate("userproperty")
                                                        }
                                                    } else {
                                                        println("La solicitud POST de imágenes seleccionadas falló: ${response.code}")
                                                    }
                                                }
                                            })
                                        } else {
                                            println("No hay imágenes seleccionadas para cargar.")
                                        }
                                    } else {
                                        println("La solicitud POST de datos de propiedad falló: ${response.code}")
                                    }
                                }
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )



            }
        }



        BottomMenu(
            selectedScreen = Screen.Home,
            onScreenSelected = { /* No hacer nada */ },
            navController = navController
        )
    }
}


@Composable
fun ImagePickerDialog(onImagesSelected: (List<String>) -> Unit) {
    val selectedImages = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    val activityResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val filePath = getPathFromUri(context, uri)
                    if (filePath != null) {
                        selectedImages.add(filePath)
                    }
                }
                if (selectedImages.isNotEmpty()) {
                    onImagesSelected(selectedImages.toList())
                    selectedImages.clear()
                }
            } ?: result.data?.data?.let { uri ->
                val filePath = getPathFromUri(context, uri)
                if (filePath != null) {
                    selectedImages.add(filePath)
                    onImagesSelected(selectedImages.toList())
                    selectedImages.clear()
                }
            }
        }
    }

    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResultLauncher.launch(intent)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(Color(0xFFB1C8F7)),
    ) {
        Text("Seleccionar desde Galería")
    }
}

fun getPathFromUri(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return it.getString(columnIndex)
        }
    }
    return null
}


// Constantes para los códigos de solicitud
private const val PICK_IMAGES_REQUEST = 123
private const val TAKE_PHOTO_REQUEST = 124



















@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text // Teclado predeterminado de texto
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Color.White) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}



@Composable
fun PropertyCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkboxColor: Color = Color.LightGray // Color del checkbox personalizable, predeterminado a gris claro
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 8.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = checkboxColor,
                uncheckedColor = checkboxColor // Hace que el color del checkbox sea siempre igual
            )
        )
        Text(label, color = Color.White) // El texto sigue siendo blanco
    }
}



@Composable
fun PropertyDropdown(label: String, selectedItem: String, onItemSelected: (String) -> Unit, items: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(vertical = 8.dp)) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White) // Fondo blanco para el botón
        ) {
            Text(text = if (selectedItem.isNotEmpty()) selectedItem else label, color = Color.Black) // Mostrar el valor seleccionado o el label
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier.background(if (item == selectedItem) Color.White else Color.Transparent)
                ) {
                    Text(
                        text = item,
                        color = if (item == selectedItem) Color.Black else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun AddPropertyButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFBED0FF)),
        content = {
            Text(
                text = "Añadir Propiedad",
                color = Color.Black
            )
        })
}


@Composable
fun CounterField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    decrementButtonColor: Color = Color.White,
    incrementButtonColor: Color = Color.White,
    textColor: Color = Color.White
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = textColor)
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = value > 0,
            modifier = Modifier
                .background(decrementButtonColor, CircleShape)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = Color.Black
            )
        }
        Text(
            text = value.toString(),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        IconButton(
            onClick = { onValueChange(value + 1) },
            modifier = Modifier
                .background(incrementButtonColor, CircleShape)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = Color.Black
            )
        }
    }
}






fun getUnsafeOkHttpClient(): OkHttpClient {
    try {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        })

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder().apply {
            sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            hostnameVerifier { _, _ -> true }
        }.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}