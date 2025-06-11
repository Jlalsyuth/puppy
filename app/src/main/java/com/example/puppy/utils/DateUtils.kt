package com.example.puppy.utils

import android.util.Log
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException

/**
 * Fungsi utilitas terpusat untuk menghitung umur dari string tanggal lahir.
 */
fun calculateAge(birthDateStr: String): String {
    if (birthDateStr.isBlank()) return "Unknown Age"
    return try {
        val birthDate = LocalDate.parse(birthDateStr)
        val today = LocalDate.now()
        val period = Period.between(birthDate, today)

        when {
            period.years > 0 -> "${period.years} year${if (period.years > 1) "s" else ""}"
            period.months > 0 -> "${period.months} month${if (period.months > 1) "s" else ""}"
            else -> {
                val days = java.time.temporal.ChronoUnit.DAYS.between(birthDate, today)
                "$days day${if (days != 1L) "s" else ""}"
            }
        }
    } catch (e: DateTimeParseException) {
        Log.e("CalculateAge", "Invalid date format for '$birthDateStr'", e)
        "Unknown Age"
    }
}
