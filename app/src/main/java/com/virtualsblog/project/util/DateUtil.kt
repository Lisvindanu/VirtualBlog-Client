package com.virtualsblog.project.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {
    
    private const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val API_DATE_FORMAT_ALT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val INPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DETAIL_FORMAT = "dd MMMM yyyy, HH:mm"
    
    /**
     * Parse date string dari API response
     */
    fun parseApiDate(dateString: String): Date? {
        return try {
            val formatWithMillis = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
            formatWithMillis.timeZone = TimeZone.getTimeZone("UTC")
            formatWithMillis.parse(dateString)
        } catch (e: Exception) {
            try {
                val formatWithoutMillis = SimpleDateFormat(API_DATE_FORMAT_ALT, Locale.getDefault())
                formatWithoutMillis.timeZone = TimeZone.getTimeZone("UTC")
                formatWithoutMillis.parse(dateString)
            } catch (e2: Exception) {
                null
            }
        }
    }
    
    /**
     * Get timestamp untuk sorting
     */
    fun getTimestamp(dateString: String): Long {
        return parseApiDate(dateString)?.time ?: 0L
    }
    
    /**
     * Format relative time (e.g., "2 jam yang lalu")
     * Optimized for newest-first display
     */
    fun getRelativeTime(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(dateString) ?: return dateString
            
            val now = System.currentTimeMillis()
            val diff = now - date.time
            
            when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Baru saja"
                diff < TimeUnit.HOURS.toMillis(1) -> {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                    "$minutes menit yang lalu"
                }
                diff < TimeUnit.DAYS.toMillis(1) -> {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    "$hours jam yang lalu"
                }
                diff < TimeUnit.DAYS.toMillis(7) -> {
                    val days = TimeUnit.MILLISECONDS.toDays(diff)
                    "$days hari yang lalu"
                }
                else -> {
                    val detailSdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    detailSdf.format(date)
                }
            }
        } catch (e: Exception) {
            dateString
        }
    }
    
    /**
     * Format date untuk detail post (e.g., "1 Juni 2025 pukul 14:17")
     */
    fun formatDateForDetail(dateString: String): String {
        return try {
            val inputSdf = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault())
            inputSdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputSdf.parse(dateString) ?: return dateString
            
            val outputSdf = SimpleDateFormat(DETAIL_FORMAT, Locale("id", "ID"))
            outputSdf.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Format date untuk card (e.g., "1 Jun 2025")
     */
    fun formatDateForCard(dateString: String): String {
        val date = parseApiDate(dateString)
        if (date == null) return Constants.INVALID_DATE
        
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return formatter.format(date)
    }
}