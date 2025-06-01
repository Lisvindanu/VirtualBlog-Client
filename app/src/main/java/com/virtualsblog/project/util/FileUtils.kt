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
            val mimeType = getMimeType(context, uri)
            
            // Validasi tipe file seperti di AuthRepositoryImpl
            if (!isValidImageType(mimeType)) {
                return null
            }
            
            val file = File(context.cacheDir, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Validasi ukuran file seperti AuthRepositoryImpl
            if (file.length() > Constants.MAX_IMAGE_SIZE) {
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
        var name = "post_image_${System.currentTimeMillis()}.jpg"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }
    
    private fun getMimeType(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            contentResolver.getType(uri) ?: run {
                // Fallback: get mime type dari file extension
                val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // Validasi tipe file konsisten dengan AuthRepositoryImpl
    private fun isValidImageType(mimeType: String?): Boolean {
        val allowedTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        return mimeType != null && allowedTypes.contains(mimeType.lowercase())
    }
    
    fun isValidImageFile(file: File): Boolean {
        val validExtensions = listOf("jpg", "jpeg", "png")
        val extension = file.extension.lowercase()
        return validExtensions.contains(extension) && 
               file.length() <= Constants.MAX_IMAGE_SIZE &&
               file.exists()
    }
    
    // Get proper mime type untuk MultipartBody - konsisten dengan AuthRepositoryImpl
    fun getImageMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "image/jpeg" // default fallback
        }
    }
}
