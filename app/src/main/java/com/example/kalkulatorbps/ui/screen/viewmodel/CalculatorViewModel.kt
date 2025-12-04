package com.example.kalkulatorbps.ui.screen.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Locale
import com.example.kalkulatorbps.utils.NumberUtils

// Enum tetap sama
enum class PadiOutputMetric { GKP, GKG, BERAS }
enum class JagungOutputMetric { LKK, JPK }
enum class SatuanBerat { KW_HA, TON_HA, KG_HA }

data class CalculatorUiState(
    val isSettingsOpen: Boolean = false,

    // INPUT (String diformat: "1.000,50")
    val padiGross: String = "",
    val padiTare: String = "",
    val faktorGkpGkg: String = "84,25", // Default pakai koma
    val faktorGkgBeras: String = "61,99", // Default pakai koma

    val jagungGross: String = "",
    val jagungTare: String = "",
    val faktorLkkJpk: String = "55,94", // Default pakai koma

    // OPSI
    val selectedPadiMetric: PadiOutputMetric = PadiOutputMetric.GKG,
    val selectedPadiUnit: SatuanBerat = SatuanBerat.TON_HA,
    val selectedJagungMetric: JagungOutputMetric = JagungOutputMetric.JPK,
    val selectedJagungUnit: SatuanBerat = SatuanBerat.TON_HA,

    // HASIL (OUTPUT)
    val padiMainDisplay: String = "0",
    val padiSubDisplay: String = "Tekan tombol Hitung", // Default state
    val valGkpTon: Double = 0.0,
    val valGkgTon: Double = 0.0,
    val valBerasTon: Double = 0.0,

    val jagungMainDisplay: String = "0",
    val jagungSubDisplay: String = "Tekan tombol Hitung", // Default state
    val valLkkTon: Double = 0.0,
    val valJpkTon: Double = 0.0
) {
    fun formatResultNumber(value: Double, decimals: Int = 2): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = decimals
        formatter.minimumFractionDigits = 0
        return formatter.format(value)
    }
}

class CalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState = _uiState.asStateFlow()

    // --- ACTIONS INPUT (Dengan Auto-Format) ---
    // Setiap kali user mengetik, format dulu string-nya baru simpan ke state

    fun onPadiGrossChanged(v: String) = updateState { it.copy(padiGross = NumberUtils.formatInputToIndonesian(v)) }
    fun onPadiTareChanged(v: String) = updateState { it.copy(padiTare = NumberUtils.formatInputToIndonesian(v)) }

    // Khusus Settings (Faktor Konversi)
    fun onFaktorGkpGkgChanged(v: String) = updateState { it.copy(faktorGkpGkg = NumberUtils.formatInputToIndonesian(v)) }
    fun onFaktorGkgBerasChanged(v: String) = updateState { it.copy(faktorGkgBeras = NumberUtils.formatInputToIndonesian(v)) }

    fun onJagungGrossChanged(v: String) = updateState { it.copy(jagungGross = NumberUtils.formatInputToIndonesian(v)) }
    fun onJagungTareChanged(v: String) = updateState { it.copy(jagungTare = NumberUtils.formatInputToIndonesian(v)) }
    fun onFaktorLkkJpkChanged(v: String) = updateState { it.copy(faktorLkkJpk = NumberUtils.formatInputToIndonesian(v)) }

    // Saat ganti satuan, update tampilan hasil tapi TIDAK menghitung ulang dari nol, melainkan mengambil nilai hasil perhitungan terakhir yg tersimpan
    fun onPadiMetricChanged(m: PadiOutputMetric) {
        _uiState.update {
            it.copy(
                selectedPadiMetric = m,
                padiMainDisplay = recalculateDisplayPadi(it, m, it.selectedPadiUnit)
            )
        }
    }

    fun onPadiUnitChanged(u: SatuanBerat) {
        _uiState.update {
            it.copy(
                selectedPadiUnit = u,
                padiMainDisplay = recalculateDisplayPadi(it, it.selectedPadiMetric, u)
            )
        }
    }

    fun onJagungMetricChanged(m: JagungOutputMetric) {
        _uiState.update {
            it.copy(
                selectedJagungMetric = m,
                jagungMainDisplay = recalculateDisplayJagung(it, m, it.selectedJagungUnit)
            )
        }
    }

    fun onJagungUnitChanged(u: SatuanBerat) {
        _uiState.update {
            it.copy(
                selectedJagungUnit = u,
                jagungMainDisplay = recalculateDisplayJagung(it, it.selectedJagungMetric, u)
            )
        }
    }

    fun toggleSettings(isOpen: Boolean) = updateState { it.copy(isSettingsOpen = isOpen) }

    private fun updateState(action: (CalculatorUiState) -> CalculatorUiState) {
        _uiState.update(action)
    }

    // Logic Hitung (Hanya dipanggil Button)

    fun calculatePadi() {
        val s = _uiState.value

        // Parse String Indo (1.000,5) ke Double (1000.5)
        val pGross = NumberUtils.parseIndonesianToDouble(s.padiGross)
        val pTare = NumberUtils.parseIndonesianToDouble(s.padiTare)
        val pNetto = (pGross - pTare).coerceAtLeast(0.0)

        // Rumus Ubinan (2.5x2.5) -> Ha: Kali 1600
        val gkpKgHa = pNetto * 1600.0
        val gkpTonHa = gkpKgHa / 1000.0

        val pctGkpGkg = NumberUtils.parseIndonesianToDouble(s.faktorGkpGkg) / 100.0
        val pctGkgBeras = NumberUtils.parseIndonesianToDouble(s.faktorGkgBeras) / 100.0

        val gkgTonHa = gkpTonHa * pctGkpGkg
        val berasTonHa = gkgTonHa * pctGkgBeras

        _uiState.update {
            val newState = it.copy(
                valGkpTon = gkpTonHa,
                valGkgTon = gkgTonHa,
                valBerasTon = berasTonHa,
                padiSubDisplay = "Netto Ubinan: ${it.formatResultNumber(pNetto)} Kg"
            )
            // Update display angka besar
            newState.copy(padiMainDisplay = recalculateDisplayPadi(newState, s.selectedPadiMetric, s.selectedPadiUnit))
        }
    }

    fun calculateJagung() {
        val s = _uiState.value

        val jGross = NumberUtils.parseIndonesianToDouble(s.jagungGross)
        val jTare = NumberUtils.parseIndonesianToDouble(s.jagungTare)
        val jNetto = (jGross - jTare).coerceAtLeast(0.0)

        val lkkKgHa = jNetto * 1600.0
        val lkkTonHa = lkkKgHa / 1000.0

        val pctLkkJpk = NumberUtils.parseIndonesianToDouble(s.faktorLkkJpk) / 100.0
        val jpkTonHa = lkkTonHa * pctLkkJpk

        _uiState.update {
            val newState = it.copy(
                valLkkTon = lkkTonHa,
                valJpkTon = jpkTonHa,
                jagungSubDisplay = "Netto Ubinan: ${it.formatResultNumber(jNetto)} Kg"
            )
            newState.copy(jagungMainDisplay = recalculateDisplayJagung(newState, s.selectedJagungMetric, s.selectedJagungUnit))
        }
    }

    // Helper untuk update tampilan saat user ganti satuan (Tanpa hitung ulang input)
    private fun recalculateDisplayPadi(state: CalculatorUiState, metric: PadiOutputMetric, unit: SatuanBerat): String {
        val baseTon = when(metric) {
            PadiOutputMetric.GKP -> state.valGkpTon
            PadiOutputMetric.GKG -> state.valGkgTon
            PadiOutputMetric.BERAS -> state.valBerasTon
        }
        return formatResult(baseTon, unit, state)
    }

    private fun recalculateDisplayJagung(state: CalculatorUiState, metric: JagungOutputMetric, unit: SatuanBerat): String {
        val baseTon = when(metric) {
            JagungOutputMetric.LKK -> state.valLkkTon
            JagungOutputMetric.JPK -> state.valJpkTon
        }
        return formatResult(baseTon, unit, state)
    }

    private fun formatResult(tonVal: Double, unit: SatuanBerat, state: CalculatorUiState): String {
        val finalVal = when(unit) {
            SatuanBerat.TON_HA -> tonVal
            SatuanBerat.KW_HA -> tonVal * 10.0
            SatuanBerat.KG_HA -> tonVal * 1000.0
        }
        val decimals = if (unit == SatuanBerat.KG_HA) 0 else 2
        return state.formatResultNumber(finalVal, decimals)
    }
}