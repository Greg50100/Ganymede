package com.joviansapps.ganymede.ui.screens.converter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.viewmodel.ConverterViewModel

@Composable
fun ConverterScreen(vm: ConverterViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.input,
            onValueChange = { vm.onInputChange(it) },
            label = { Text(stringResource(R.string.converter_input_label, state.unitFrom)) }
        )
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.converter_result_format, state.output, state.unitTo))
    }
}