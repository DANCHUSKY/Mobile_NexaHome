
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.BottomMenu
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@SuppressLint("CoroutineCreationDuringComposition", "BlockingMethodInNonBlockingContext")
@Composable
fun UserScreen(navController: NavHostController) {
    // Variable mutable para almacenar el nombre del perfil.
    var profileName by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }

    // Obtiene el token del usuario almacenado.
    val token = UserTokenHolder.token

    LaunchedEffect(token) {
        if (token != null) {
            try {
                val url = "https://192.168.0.23:7770/myInfo"
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
                    val jsonObject = JSONObject(responseData)
                    println("Informacion")
                    println(jsonObject.toString())
                    profileName = jsonObject.getString("name")
                } else {
                    showError = true
                }
            } catch (e: IOException) {
                showError = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Espacio para centrar verticalmente

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            // Mostrar el nombre del perfil o "PERFIL" si no se ha cargado.
            Text(
                text = profileName ?: "PERFIL",
                fontSize = 32.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Espacio para centrar verticalmente
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileOption(text = "Tus Propiedades", icon = Icons.Default.Home, color = Color.White, navController = navController)
            ProfileOption(text = "Editar perfil", icon = Icons.Default.Edit, color = Color.White, navController = navController)
            ProfileOption(text = "Cambiar contraseña", icon = Icons.Default.Lock, color = Color.White, navController = navController)
            ProfileOption(text = "Redes Sociales", icon = Icons.Default.SendToMobile, color = Color.White, navController = navController)
            ProfileOption(text = "Cerrar sesión", icon = Icons.Default.ExitToApp, color = Color(0xFFFD4949), navController = navController)
        }
        Spacer(modifier = Modifier.weight(1f)) // Espacio para centrar verticalmente
        BottomMenu(selectedScreen = Screen.Profile, onScreenSelected = { /* No hacer nada */ }, navController = navController)
    }

    if (showError) {
        // Mostrar algún mensaje de error o realizar otra acción si ocurre un error.
        Toast.makeText(LocalContext.current, "Error al cargar el perfil", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun ProfileOption(text: String, icon: ImageVector, color: Color = Color.Black, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                when (text) {
                    "Cambiar contraseña" -> navController.navigate("password")
                    "Tus Propiedades" -> navController.navigate("userproperty")
                    "Redes Sociales" -> navController.navigate("informationscreen")
                    "Cerrar sesión" -> {
                        UserTokenHolder.token = null
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
            .background(
                color = Color.Black, // Color de fondo
                shape = RoundedCornerShape(8.dp) // Forma del fondo redondeada
            )
            .padding(8.dp) // Espaciado interno para el fondo
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color // Color del icono
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            color = color, // Color del texto
            modifier = Modifier.weight(1f)
        )
    }
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
