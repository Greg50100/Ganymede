package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import android.content.Context
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

data class SymbolInfo(val componentName: String, val iecResId: Int, val ansiResId: Int)

private enum class Standard { IEC, ANSI, BOTH }

private enum class Category(val title: String) {
    PASSIFS("Passifs"),
    DIODES("Diodes"),
    TRANSISTORS("Transistors"),
    AUTRES("Autres"),
}

private fun categoryFor(symbol: SymbolInfo): Category = when (symbol.componentName.lowercase()) {
    in listOf("résistance", "resistance", "condensateur", "capacitor", "inductance", "inductor") -> Category.PASSIFS
    in listOf("diode", "zener diode", "zener") -> Category.DIODES
    in listOf("bjt npn", "bjt pnp", "transistor npn", "transistor pnp") -> Category.TRANSISTORS
    else -> Category.AUTRES
}

// DataStore (préférences persistées)
private val Context.electronicsDataStore by preferencesDataStore(name = "electronics_symbols")
private val KEY_STANDARD = stringPreferencesKey("standard")
private val KEY_COMPACT = booleanPreferencesKey("compact")

private class Prefs(private val context: Context) {
    val standardFlow = context.electronicsDataStore.data.map { prefs ->
        when (prefs[KEY_STANDARD]) {
            Standard.IEC.name -> Standard.IEC
            Standard.ANSI.name -> Standard.ANSI
            else -> Standard.IEC // IEC par défaut au lieu de BOTH
        }
    }
    val compactFlow = context.electronicsDataStore.data.map { prefs ->
        prefs[KEY_COMPACT] ?: true
    }
    suspend fun setStandard(std: Standard) {
        context.electronicsDataStore.edit { it[KEY_STANDARD] = std.name }
    }
    suspend fun setCompact(value: Boolean) {
        context.electronicsDataStore.edit { it[KEY_COMPACT] = value }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun ElectronicSymbolsScreen() {
    val context = LocalContext.current
    val prefs = remember(context) { Prefs(context) }
    val scope = rememberCoroutineScope()

    // Catalogue des symboles étendu
    val allSymbols = remember {
        listOf(
            // Passifs
            SymbolInfo("Résistance", R.drawable.ic_iec_resistor, R.drawable.ic_ansi_resistor),
            SymbolInfo("Condensateur", R.drawable.ic_iec_capacitor, R.drawable.ic_ansi_capacitor),
            SymbolInfo("Inductance", R.drawable.ic_iec_inductor, R.drawable.ic_ansi_inductor),
            // Diodes
            SymbolInfo("Diode", R.drawable.ic_iec_diode, R.drawable.ic_ansi_diode),
            SymbolInfo("LED", R.drawable.ic_iec_led, R.drawable.ic_ansi_led),
            SymbolInfo("Zener Diode", R.drawable.ic_iec_zener_diode, R.drawable.ic_ansi_zener_diode),
            // Transistors
            SymbolInfo("BJT NPN", R.drawable.ic_iec_bjt_npn, R.drawable.ic_ansi_bjt_npn),
            SymbolInfo("BJT PNP", R.drawable.ic_iec_bjt_pnp, R.drawable.ic_ansi_bjt_pnp),
            // Autres
            SymbolInfo("OpAmp", R.drawable.ic_iec_opamp, R.drawable.ic_ansi_opamp),
        )
    }

    var query by rememberSaveable { mutableStateOf("") }

    // Standard pilotée par DataStore - IEC par défaut
    val standard by prefs.standardFlow.collectAsState(initial = Standard.IEC)

    // Sélection pour zoom
    var zoom by remember { mutableStateOf<ZoomTarget?>(null) }

    val filtered = remember(query, allSymbols) {
        allSymbols.filter { it.componentName.contains(query, ignoreCase = true) }
    }
    val grouped = remember(filtered) {
        filtered.groupBy { symbol ->
            when {
                symbol.componentName in listOf("Résistance", "Condensateur", "Inductance") -> Category.PASSIFS
                symbol.componentName in listOf("Diode", "LED", "Zener Diode") -> Category.DIODES
                symbol.componentName in listOf("BJT NPN", "BJT PNP") -> Category.TRANSISTORS
                else -> Category.AUTRES
            }
        }
    }

    val orderedCats = listOf(Category.PASSIFS, Category.DIODES, Category.TRANSISTORS, Category.AUTRES)

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Barre de recherche
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                label = { Text("Rechercher un composant") },
                singleLine = true
            )

            // Filtres des standards uniquement
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    "Standard",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = standard == Standard.IEC,
                        onClick = { scope.launch { prefs.setStandard(Standard.IEC) } },
                        label = { Text("IEC") }
                    )
                    FilterChip(
                        selected = standard == Standard.ANSI,
                        onClick = { scope.launch { prefs.setStandard(Standard.ANSI) } },
                        label = { Text("ANSI") }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aucun résultat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // En-tête du tableau
                TableHeader()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    orderedCats.forEach { cat ->
                        val list = grouped[cat] ?: emptyList()
                        if (list.isNotEmpty()) {
                            stickyHeader {
                                CategoryHeader(cat.title)
                            }

                            items(list) { symbol ->
                                SymbolTableRow(
                                    symbolInfo = symbol,
                                    standard = standard,
                                    onZoom = { s -> zoom = ZoomTarget(s) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog de zoom plein écran (affiche IEC et ANSI côte à côte)
    zoom?.let { target ->
        ZoomDialog(
            symbol = target.symbol,
            onDismiss = { zoom = null }
        )
    }
}

@Composable
private fun CategoryHeader(title: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun StandardsHeader() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Norme IEC",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Norme ANSI",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class ZoomTarget(val symbol: SymbolInfo)

@Composable
private fun SymbolRowSingle(
    name: String,
    resId: Int,
    tag: String,
    compact: Boolean,
    onZoom: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onZoom() })
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = "$tag $name",
            modifier = Modifier
                .height(if (compact) 32.dp else 48.dp)
                .weight(1f),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            name,
            style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(2f)
        )
        if (!compact) {
            Spacer(Modifier.width(8.dp))
            Text(
                tag,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SymbolRowBoth(
    symbolInfo: SymbolInfo,
    compact: Boolean,
    onZoom: (resId: Int, tag: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            symbolInfo.componentName,
            style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = symbolInfo.iecResId),
            contentDescription = "IEC ${symbolInfo.componentName}",
            modifier = Modifier
                .height(if (compact) 28.dp else 40.dp)
                .weight(1f)
                .pointerInput(Unit) { detectTapGestures(onTap = { onZoom(symbolInfo.iecResId, "IEC") }) },
            tint = MaterialTheme.colorScheme.primary
        )
        if (compact) {
            Spacer(Modifier.width(4.dp))
        }
        Icon(
            painter = painterResource(id = symbolInfo.ansiResId),
            contentDescription = "ANSI ${symbolInfo.componentName}",
            modifier = Modifier
                .height(if (compact) 28.dp else 40.dp)
                .weight(1f)
                .pointerInput(Unit) { detectTapGestures(onTap = { onZoom(symbolInfo.ansiResId, "ANSI") }) },
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ZoomDialog(symbol: SymbolInfo, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        if (scale > 1f) {
            offsetX += panChange.x
            offsetY += panChange.y
        } else {
            offsetX = 0f; offsetY = 0f
        }
    }

    val iecName = try { context.resources.getResourceEntryName(symbol.iecResId) } catch (_: Exception) { "iec_resource" }
    val ansiName = try { context.resources.getResourceEntryName(symbol.ansiResId) } catch (_: Exception) { "ansi_resource" }
    val iecHex = try { String.format("0x%08X", symbol.iecResId) } catch (_: Exception) { symbol.iecResId.toString() }
    val ansiHex = try { String.format("0x%08X", symbol.ansiResId) } catch (_: Exception) { symbol.ansiResId.toString() }
    val category = categoryFor(symbol).title

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = true)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f),
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) { detectTapGestures(onDoubleTap = { scale = 1f; offsetX = 0f; offsetY = 0f }) }
        ) {
            Box(Modifier.fillMaxSize()) {
                // Close button top-right
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Zoomable content: IEC / ANSI images
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offsetX
                            translationY = offsetY
                        }
                        .transformable(state)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = symbol.iecResId),
                            contentDescription = "IEC ${symbol.componentName}",
                            modifier = Modifier.size(220.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(symbol.componentName, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(6.dp))
                        Text("IEC: $iecName", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(iecHex, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = symbol.ansiResId),
                            contentDescription = "ANSI ${symbol.componentName}",
                            modifier = Modifier.size(220.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(symbol.componentName, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(6.dp))
                        Text("ANSI: $ansiName", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(ansiHex, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Catégorie et nom global sous les schémas
                Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Text(symbol.componentName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Composant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "Symbole",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SymbolTableRow(
    symbolInfo: SymbolInfo,
    standard: Standard,
    onZoom: (SymbolInfo) -> Unit,
) {
    val resId = when (standard) {
        Standard.IEC -> symbolInfo.iecResId
        Standard.ANSI -> symbolInfo.ansiResId
        Standard.BOTH -> symbolInfo.iecResId // Ne devrait pas arriver
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onZoom(symbolInfo) })
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = symbolInfo.componentName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(2f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = resId),
                    contentDescription = "$resId ${symbolInfo.componentName}",
                    modifier = Modifier.height(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Divider entre les lignes
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

// Ancienne carte (conservée si besoin de réutiliser dans d’autres écrans)
@Composable
fun SymbolCard(symbolInfo: SymbolInfo) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(symbolInfo.componentName, style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("IEC", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        painter = painterResource(id = symbolInfo.iecResId),
                        contentDescription = "IEC Symbol for ${symbolInfo.componentName}",
                        modifier = Modifier.height(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ANSI", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        painter = painterResource(id = symbolInfo.ansiResId),
                        contentDescription = "ANSI Symbol for ${symbolInfo.componentName}",
                        modifier = Modifier.height(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
