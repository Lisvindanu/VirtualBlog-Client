package com.virtualsblog.project.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    
    private val inputFormats = arrayOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    )
    
    private val outputFormatCard = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    private val outputFormatDetail = SimpleDateFormat("dd MMMM yyyy 'pukul' HH:mm", Locale("id", "ID"))
    
    /**
     * Parse date string dari API response
     */
    fun parseApiDate(dateString: String): Date? {
        for (format in inputFormats) {
            try {
                return format.parse(dateString)
            } catch (e: Exception) {
                // Continue to next format
            }
        }
        return null
    }
    
    /**
     * Format date untuk display di card
     */
    fun formatDateForCard(dateString: String): String {
        val date = parseApiDate(dateString)
        return if (date != null) {
            outputFormatCard.format(date)
        } else {
            Constants.INVALID_DATE
        }
    }
    
    /**
     * Format date untuk display detail
     */
    fun formatDateForDetail(dateString: String): String {
        val date = parseApiDate(dateString)
        return if (date != null) {
            outputFormatDetail.format(date)
        } else {
            Constants.INVALID_DATE
        }
    }
    
    /**
     * Get timestamp untuk sorting
     */
    fun getTimestamp(dateString: String): Long {
        val date = parseApiDate(dateString)
        return date?.time ?: 0L
    }
    
    /**
     * Format relative time (e.g., "2 jam yang lalu")
     * Optimized for newest-first display
     */
    fun getRelativeTime(dateString: String): String {
        val date = parseApiDate(dateString)
        if (date == null) return Constants.INVALID_DATE
        
        val now = Date()
        val diff = now.time - date.time
        
        return when {
            diff < 60 * 1000 -> "Baru saja" // Less than 1 minute
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} menit yang lalu" // Less than 1 hour
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} jam yang lalu" // Less than 1 day
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} hari yang lalu" // Less than 1 week
            diff < 30 * 24 * 60 * 60 * 1000 -> "${diff / (7 * 24 * 60 * 60 * 1000)} minggu yang lalu" // Less than 1 month
            else -> formatDateForCard(dateString) // For very old posts, show date
        }
    }
}