package com.virtualsblog.project.domain.usecase.user

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns // Untuk mendapatkan nama file jika diperlukan
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.UserRepository
import com.virtualsblog.project.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

class UploadProfilePictureUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(context: Context, imageUri: Uri): Resource<User> {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: return Resource.Error("Gagal membuka file gambar")

            // Coba dapatkan nama file asli dari URI, jika tidak ada, buat nama generik
            var fileName = "profile_image.jpg" // Default name
            contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex)
                    }
                }
            }

            val fileBytes = inputStream.use { it.readBytes() }
            inputStream.close()

            val mimeType = contentResolver.getType(imageUri) ?: "image/*" // Fallback MIME type

            val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            // Gunakan nama "photo" sesuai dengan ekspektasi backend
            val part = MultipartBody.Part.createFormData("photo", fileName, requestBody)

            repository.uploadProfilePicture(part)
        } catch (e: IOException) {
            Resource.Error("Gagal membaca file gambar: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Terjadi kesalahan saat menyiapkan gambar: ${e.message}")
        }
    }
}