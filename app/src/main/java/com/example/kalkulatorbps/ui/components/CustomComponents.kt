package com.example.kalkulatorbps.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalkulatorbps.ui.theme.PadiDarkGreen
import com.example.kalkulatorbps.ui.theme.PadiMediumGreen
import com.example.kalkulatorbps.ui.theme.PadiSecondaryText
import com.example.kalkulatorbps.ui.theme.White

@Composable
fun PadiExpressTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String? = null,
    readOnly: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val containerColor = if (readOnly) Color(0xFFEEEEEE) else White
    val textColor = if (readOnly) Color.DarkGray else Color.Black

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 13.sp,
            color = PadiSecondaryText
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (readOnly) 0.dp else 4.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                focusedIndicatorColor = if (readOnly) Color.Transparent else PadiDarkGreen,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            // UBAH KE DECIMAL AGAR MUNCUL ANGKA & KOMA DI KEYBOARD
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            trailingIcon = {
                if (trailingContent != null) {
                    trailingContent()
                } else if (suffix != null) {
                    Text(
                        suffix,
                        modifier = Modifier.padding(end = 16.dp),
                        color = if(readOnly) Color.DarkGray else PadiSecondaryText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

// KOMPONEN BARU: TOMBOL
@Composable
fun PadiExpressButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = PadiMediumGreen,
            contentColor = White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PadiUnitToggle(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(horizontal = 4.dp)) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onSelect(label) },
                selected = label == selected,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = PadiMediumGreen,
                    activeContentColor = Color.White,
                    inactiveContainerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Text(label, fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

@Composable
fun PadiExpressOutputCard(
    label: String,
    value: String,
    unitOptions: List<String>,
    selectedUnit: String,
    onUnitSelect: (String) -> Unit,
    subValue: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PadiDarkGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 42.sp
            )

            Spacer(Modifier.height(10.dp))

            if (subValue != null) {
                Text(text= "Berat Hasil Ubinan", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Text(text = subValue, color = Color.White.copy(0.9f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            PadiUnitToggle(
                options = unitOptions,
                selected = selectedUnit,
                onSelect = onUnitSelect
            )
        }
    }
}