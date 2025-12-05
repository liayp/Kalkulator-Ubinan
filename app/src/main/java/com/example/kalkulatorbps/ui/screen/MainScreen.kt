package com.example.kalkulatorbps.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kalkulatorbps.ui.components.PadiExpressTextField
import com.example.kalkulatorbps.ui.screen.viewmodel.CalculatorViewModel
import com.example.kalkulatorbps.ui.theme.PadiDarkGreen
import com.example.kalkulatorbps.ui.theme.PadiLightCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val tabItems = listOf("Ubinan Padi", "Ubinan Jagung")
    val pagerState = rememberPagerState { tabItems.size }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    // SETTINGS (POPUP)
    if (uiState.isSettingsOpen) {
        Dialog(onDismissRequest = { viewModel.toggleSettings(false) }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Pengaturan Konversi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PadiDarkGreen)
                    Spacer(Modifier.height(16.dp))

                    Text("Padi", fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
                    PadiExpressTextField(
                        label = "GKP ke GKG",
                        value = uiState.faktorGkpGkg,
                        onValueChange = { viewModel.onFaktorGkpGkgChanged(it) },
                        suffix = "%"
                    )
                    Spacer(Modifier.height(8.dp))
                    PadiExpressTextField(
                        label = "GKG ke Beras",
                        value = uiState.faktorGkgBeras,
                        onValueChange = { viewModel.onFaktorGkgBerasChanged(it) },
                        suffix = "%"
                    )

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))

                    Text("Jagung", fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
                    PadiExpressTextField(
                        label = "LKK ke JPK",
                        value = uiState.faktorLkkJpk,
                        onValueChange = { viewModel.onFaktorLkkJpkChanged(it) },
                        suffix = "%"
                    )

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.toggleSettings(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = PadiDarkGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SIMPAN & TUTUP")
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PadiDarkGreen,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Konversi Ubinan BPS", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = { viewModel.toggleSettings(true) }) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(color = PadiLightCard)) {

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 4.dp,
                        color = PadiDarkGreen
                    )
                },
                contentColor = PadiDarkGreen,
                divider = {}
            ) {
                tabItems.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title, fontWeight = if(pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) { page ->
                when (page) {
                    0 -> PadiScreen(viewModel = viewModel)
                    1 -> JagungScreen(viewModel = viewModel)
                }
            }
        }
    }
}