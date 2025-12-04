package com.example.kalkulatorbps.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberUtils {

    fun formatInputToIndonesian(input: String): String {
        if (input.isEmpty()) return ""

        var workingString = input

        // 1. Cek apakah karakter TERAKHIR adalah titik
        // Jika iya, user baru saja mengetik titik untuk maksud desimal -> Ubah jadi koma
        if (workingString.endsWith(".")) {
            workingString = workingString.substring(0, workingString.length - 1) + ","
        }

        // 2. Hapus semua titik sisa (ini adalah titik ribuan dari format sebelumnya)
        // Contoh: "1.0000" (user nambah nol) -> jadi "10000"
        var cleanInput = workingString.replace(".", "")

        // 3. Pastikan hanya ada satu koma
        // Hapus karakter aneh selain angka dan koma
        cleanInput = cleanInput.replace(Regex("[^0-9,]"), "")

        // Cegah koma ganda (misal 12,,5)
        val firstCommaIndex = cleanInput.indexOf(',')
        if (firstCommaIndex != -1) {
            val beforeComma = cleanInput.substring(0, firstCommaIndex + 1)
            val afterComma = cleanInput.substring(firstCommaIndex + 1).replace(",", "")
            cleanInput = beforeComma + afterComma
        }

        // 4. Pisahkan Ribuan dan Desimal
        val parts = cleanInput.split(",")
        val integerPartRaw = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else null

        // 5. Format Angka Bulat (Kasih Titik Ribuan)
        val formattedInteger = if (integerPartRaw.isNotEmpty()) {
            try {
                val number = integerPartRaw.toLong()
                val symbols = DecimalFormatSymbols(Locale("id", "ID"))
                val formatter = DecimalFormat("#,###", symbols)
                formatter.format(number)
            } catch (e: Exception) {
                integerPartRaw // Fallback jika angka terlalu besar
            }
        } else {
            if (decimalPart != null) "0" else ""
        }

        // 6. Gabungkan Kembali
        return if (decimalPart != null) {
            "$formattedInteger,$decimalPart"
        } else {
            // Jika user baru mengetik koma di akhir (misal "1.000,")
            if (cleanInput.endsWith(",")) {
                "$formattedInteger,"
            } else {
                formattedInteger
            }
        }
    }

    fun parseIndonesianToDouble(input: String): Double {
        if (input.isEmpty()) return 0.0
        // Hapus titik ribuan, ubah koma desimal jadi titik komputer
        val cleanString = input.replace(".", "").replace(",", ".")
        return cleanString.toDoubleOrNull() ?: 0.0
    }
}