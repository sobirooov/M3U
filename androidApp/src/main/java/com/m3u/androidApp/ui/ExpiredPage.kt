package com.m3u.androidApp.ui

import Video
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.m3u.androidApp.pocketbase.PocketBaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.system.exitProcess

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExpiredPage(message: String, androidId: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Check if width is larger than height (landscape mode)
            val isLandscape = maxWidth > maxHeight

            val horizontalModifier = if (isLandscape) Modifier.fillMaxWidth(0.7f) else Modifier.fillMaxWidth()

            Column(
                modifier = Modifier
                    .then(horizontalModifier)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    // Generate QR code with the link
                    val qrCodeBitmap = generateQrCodeBitmap("https://t.me/ipxtvbot?start=$androidId")

                    // Display the QR code if it's successfully generated
                    qrCodeBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(200.dp) // Adjust size as needed
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Video()
                }

                // Display the Android ID prominently
                Text(
                    text = "ИД: $androidId",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Логин") },
                    shape = MaterialTheme.shapes.medium, // Rounded corners
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Парол") },
                    shape = MaterialTheme.shapes.medium, // Rounded corners
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Login button
                Button(
                    onClick = {
                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = PocketBaseApi.service.login(
                                    mapOf(
                                        "username" to username,
                                        "password" to password,
                                        "android_id" to androidId
                                    )
                                )
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        activity?.let {
                                            it.finish()
                                            it.startActivity(it.intent)
                                            exitProcess(0)
                                        }
                                    } else {
                                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: HttpException) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Тизимга кириш")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Reload button
                Button(
                    onClick = {
                        activity?.let {
                            it.finish()
                            it.startActivity(it.intent)
                            exitProcess(0) // Optionally close the app completely
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Қайта уриниш")
                }
            }
        }
    }
}

/**
 * Generates a QR code bitmap for the given content.
 * @param content The content to encode in the QR code.
 * @return A [Bitmap] representing the QR code, or null if generation fails.
 */
fun generateQrCodeBitmap(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
