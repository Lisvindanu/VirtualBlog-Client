package com.virtualsblog.project.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    
    // Pattern yang sama dengan file handling di AuthRepositoryImpl
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri)
            
            // Buat file dengan ekstensi yang benar
            val file = File(context.cacheDir, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Validasi setelah file dibuat
            if (!isValidImageFile(file)) {
                file.delete()
                return null
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun getFileName(context: Context, uri: Uri): String {
        var name = "post_image_${System.currentTimeMillis()}"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    val originalName = it.getString(nameIndex)
                    if (originalName != null) {
                        name = originalName
                    }
                }
            }
        }
        
        // Pastikan ada ekstensi yang valid
        if (!name.contains(".")) {
            name = "${name}.jpg"
        }
        
        return name
    }
    
    fun isValidImageFile(file: File): Boolean {
        if (!file.exists() || file.length() == 0L) {
            return false
        }
        
        // Validasi ukuran file maksimal 10MB seperti AuthRepositoryImpl
        if (file.length() > Constants.MAX_IMAGE_SIZE) {
            return false
        }
        
        val validExtensions = listOf("jpg", "jpeg", "png")
        val extension = file.extension.lowercase()
        
        // Jika tidak ada ekstensi, coba detect dari content
        if (extension.isEmpty()) {
            return isValidImageByContent(file)
        }
        
        return validExtensions.contains(extension)
    }
    
    private fun isValidImageByContent(file: File): Boolean {
        return try {
            val bytes = file.readBytes()
            if (bytes.size < 4) return false
            
            // Check JPEG signature - lebih comprehensive
            if (bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() && 
                bytes[2] == 0xFF.toByte()) {
                return true
            }
            
            // Check PNG signature - 8 bytes signature
            if (bytes.size >= 8 &&
                bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && 
                bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte() &&
                bytes[4] == 0x0D.toByte() && bytes[5] == 0x0A.toByte() &&
                bytes[6] == 0x1A.toByte() && bytes[7] == 0x0A.toByte()) {
                return true
            }
            
            false
        } catch (e: Exception) {
            false
        }
    }
    
    // Get proper mime type untuk MultipartBody - konsisten dengan AuthRepositoryImpl
    fun getImageMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return when {
            extension == "jpg" || extension == "jpeg" -> "image/jpeg"
            extension == "png" -> "image/png"
            else -> {
                // Fallback: detect by content
                detectMimeTypeByContent(file)
            }
        }
    }
    
    private fun detectMimeTypeByContent(file: File): String {
        return try {
            val bytes = file.readBytes()
            if (bytes.size >= 8) {
                // Check PNG signature
                if (bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && 
                    bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte() &&
                    bytes[4] == 0x0D.toByte() && bytes[5] == 0x0A.toByte() &&
                    bytes[6] == 0x1A.toByte() && bytes[7] == 0x0A.toByte()) {
                    return "image/png"
                }
                
                // Check JPEG signature - more complete check
                if (bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() && 
                    bytes[2] == 0xFF.toByte()) {
                    return "image/jpeg"
                }
            }
            "image/jpeg" // default fallback
        } catch (e: Exception) {
            "image/jpeg" // default fallback
        }
    }
}
