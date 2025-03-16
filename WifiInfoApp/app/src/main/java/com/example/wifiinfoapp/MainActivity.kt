package com.example.wifiinfoapp

import android.Manifest
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wifiinfoapp.ui.theme.WifiInfoAppTheme
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiInfoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WifiInfoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WifiInfoScreen(modifier: Modifier = Modifier) {
    var wifiInfo by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Request permission to access location
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                wifiInfo = getWifiInfo(context)
            } else {
                wifiInfo = "Permission denied. Unable to access Wi-Fi information."
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }) {
            Text(text = "Show Wi-Fi Info")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = wifiInfo)
    }
}

private fun getWifiInfo(context: Context): String {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo: WifiInfo = wifiManager.connectionInfo

    val ssid = wifiInfo.ssid
    val bssid = wifiInfo.bssid
    val ipAddress = intToIp(wifiInfo.ipAddress)

    return "SSID: $ssid\nBSSID: $bssid\nIP Address: $ipAddress"
}

private fun intToIp(ipAddress: Int): String {
    return (ipAddress and 0xFF).toString() + "." +
            ((ipAddress shr 8) and 0xFF).toString() + "." +
            ((ipAddress shr 16) and 0xFF).toString() + "." +
            ((ipAddress shr 24) and 0xFF).toString()
}