package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class PortInfo(val port: Int, val protocol: String, val service: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TcpUdpPortsScreen(onBack: (() -> Unit)? = null) {
    val ports = listOf(
        PortInfo(20, "TCP", "FTP (Data)"),
        PortInfo(21, "TCP", "FTP (Control)"),
        PortInfo(22, "TCP", "SSH"),
        PortInfo(23, "TCP", "Telnet"),
        PortInfo(25, "TCP", "SMTP"),
        PortInfo(53, "TCP/UDP", "DNS"),
        PortInfo(80, "TCP", "HTTP"),
        PortInfo(110, "TCP", "POP3"),
        PortInfo(143, "TCP", "IMAP"),
        PortInfo(443, "TCP", "HTTPS"),
        PortInfo(3389, "TCP", "RDP")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.tcp_udp_ports)) },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item { PortHeader() }
            items(ports) { port ->
                PortRow(info = port)
            }
        }
    }
}

@Composable
private fun PortHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp)
    ) {
        Text("Port", modifier = Modifier.weight(1f).padding(start = 8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        Text("Protocol", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        Text("Service", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun PortRow(info: PortInfo) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(info.port.toString(), modifier = Modifier.weight(1f).padding(start = 8.dp))
        Text(info.protocol, modifier = Modifier.weight(1f))
        Text(info.service, modifier = Modifier.weight(2f))
    }
}
