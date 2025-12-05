package com.example.kalkulatorbps.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalkulatorbps.ui.components.*
import com.example.kalkulatorbps.ui.screen.viewmodel.*
import com.example.kalkulatorbps.ui.theme.PadiDarkGreen

@Composable
fun JagungScreen(viewModel: CalculatorViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        PadiExpressTextField(
            label = "Berat Ubinan",
            value = uiState.jagungGross,
            onValueChange = { viewModel.onJagungGrossChanged(it) },
            suffix = "Kg"
        )

        Spacer(Modifier.height(12.dp))

        PadiExpressTextField(
            label = "Berat Karung",
            value = uiState.jagungTare,
            onValueChange = { viewModel.onJagungTareChanged(it) },
            suffix = "Kg"
        )

        Spacer(Modifier.height(24.dp))

        // BUTTON HITUNG
        PadiExpressButton(
            text = "HITUNG",
            onClick = { viewModel.calculateJagung() }
        )

        Spacer(Modifier.height(24.dp))

        Text("Hasil Produktivitas", color = PadiDarkGreen, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = uiState.selectedJagungMetric.ordinal,
            containerColor = Color.Transparent,
            contentColor = PadiDarkGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[uiState.selectedJagungMetric.ordinal]),
                    color = PadiDarkGreen
                )
            }
        ) {
            JagungOutputMetric.values().forEach { metric ->
                val label = if(metric == JagungOutputMetric.LKK) "LKK (Tongkol)" else "JPK (Pipilan)"
                Tab(
                    selected = uiState.selectedJagungMetric == metric,
                    onClick = { viewModel.onJagungMetricChanged(metric) },
                    text = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        PadiExpressOutputCard(
            label = "ESTIMASI ${uiState.selectedJagungMetric}",
            value = uiState.jagungMainDisplay,
            subValue = uiState.jagungSubDisplay,
            unitOptions = listOf("Ton/Ha", "Kw/Ha", "Kg/Ha"),
            selectedUnit = when (uiState.selectedJagungUnit) {
                SatuanBerat.TON_HA -> "Ton/Ha"
                SatuanBerat.KW_HA -> "Kw/Ha"
                SatuanBerat.KG_HA -> "Kg/Ha"
            },
            onUnitSelect = { str ->
                val unit = when (str) {
                    "Kw/Ha" -> SatuanBerat.KW_HA
                    "Kg/Ha" -> SatuanBerat.KG_HA
                    else -> SatuanBerat.TON_HA
                }
                viewModel.onJagungUnitChanged(unit)
            }
        )

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Ringkasan (Ton/Ha)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("LKK (Lepas Kulit Kering)", fontSize = 13.sp, color = Color.Black)
                    Text(uiState.formatResultNumber(uiState.valLkkTon), fontWeight = FontWeight.Bold, color = Color.Black)
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Color.LightGray)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("JPK (Jagung Pipilan Kering)", fontSize = 13.sp, color = Color.Black)
                    Text(uiState.formatResultNumber(uiState.valJpkTon), fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}