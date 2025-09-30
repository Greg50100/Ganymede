package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class HttpCode(val code: Int, val message: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HttpCodesScreen() {
    val httpCodes = mapOf(
        "1xx Informational" to listOf(
            HttpCode(100, "Continue")
        ),
        "2xx Success" to listOf(
            HttpCode(200, "OK"),
            HttpCode(201, "Created"),
            HttpCode(204, "No Content")
        ),
        "3xx Redirection" to listOf(
            HttpCode(301, "Moved Permanently"),
            HttpCode(302, "Found"),
            HttpCode(304, "Not Modified")
        ),
        "4xx Client Error" to listOf(
            HttpCode(400, "Bad Request"),
            HttpCode(401, "Unauthorized"),
            HttpCode(403, "Forbidden"),
            HttpCode(404, "Not Found")
        ),
        "5xx Server Error" to listOf(
            HttpCode(500, "Internal Server Error"),
            HttpCode(502, "Bad Gateway"),
            HttpCode(503, "Service Unavailable")
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.http_status_codes)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            httpCodes.forEach { (category, codes) ->
                stickyHeader {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(codes) { code ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("${code.code}", fontWeight = FontWeight.Bold)
                        Text(code.message, style = MaterialTheme.typography.body2)
                    }
                }
            }
        }
    }
}
