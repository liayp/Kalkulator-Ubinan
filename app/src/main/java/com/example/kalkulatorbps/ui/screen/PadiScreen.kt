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
fun PadiScreen(viewModel: CalculatorViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())
    ) {

        PadiExpressTextField(
            label = "Berat Kotor",
            value = uiState.padiGross,
            onValueChange = { viewModel.onPadiGrossChanged(it) }, // Ini udah auto format
            suffix = "Kg"
        )

        Spacer(Modifier.height(12.dp))

        PadiExpressTextField(
            label = "Berat Karung",
            value = uiState.padiTare,
            onValueChange = { viewModel.onPadiTareChanged(it) },
            suffix = "Kg"
        )

        Spacer(Modifier.height(24.dp))

        // BUTTON HITUNG
        PadiExpressButton(
            text = "HITUNG",
            onClick = { viewModel.calculatePadi() }
        )

        Spacer(Modifier.height(24.dp))

        Text("Hasil Produktivitas", color = PadiDarkGreen, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = uiState.selectedPadiMetric.ordinal,
            containerColor = Color.Transparent,
            contentColor = PadiDarkGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[uiState.selectedPadiMetric.ordinal]),
                    color = PadiDarkGreen
                )
            }
        ) {
            PadiOutputMetric.entries.forEach { metric ->
                Tab(
                    selected = uiState.selectedPadiMetric == metric,
                    onClick = { viewModel.onPadiMetricChanged(metric) },
                    text = { Text(metric.name, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        PadiExpressOutputCard(
            label = "ESTIMASI ${uiState.selectedPadiMetric}",
            value = uiState.padiMainDisplay,
            subValue = uiState.padiSubDisplay,
            unitOptions = listOf("Ton/Ha", "Kw/Ha", "Kg/Ha"),
            selectedUnit = when (uiState.selectedPadiUnit) {
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
                viewModel.onPadiUnitChanged(unit)
            }
        )

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7F4)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Ringkasan (Ton/Ha)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PadiDarkGreen)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GKP", fontSize = 13.sp)
                    Text(uiState.formatResultNumber(uiState.valGkpTon), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Color.LightGray)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GKG", fontSize = 13.sp)
                    Text(uiState.formatResultNumber(uiState.valGkgTon), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Color.LightGray)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Beras", fontSize = 13.sp)
                    Text(uiState.formatResultNumber(uiState.valBerasTon), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}