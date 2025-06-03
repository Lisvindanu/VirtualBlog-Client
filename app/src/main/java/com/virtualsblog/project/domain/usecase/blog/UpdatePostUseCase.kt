package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(
        postId: String,
        title: String,
        content: String,
        photo: File?
    ): Flow<Resource<Post>> {
        if (postId.isBlank()) {
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("Post ID tidak boleh kosong.")) }
        }
        // API validation for title is min 3, content min 10
        if (title.trim().length < 3) { // Matching API doc for edit
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("Judul minimal 3 karakter.")) }
        }
        if (content.trim().length < 10) { // Matching API doc for edit
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("Konten minimal 10 karakter.")) }
        }
        photo?.let {
            if (it.length() > Constants.MAX_IMAGE_SIZE) { // MAX_IMAGE_SIZE is 10MB
                return kotlinx.coroutines.flow.flow { emit(Resource.Error("Ukuran gambar maksimal 10MB.")) }
            }
            val allowedExtensions = listOf("jpg", "jpeg", "png")
            val fileExtension = photo.extension.lowercase()
            if (!allowedExtensions.contains(fileExtension)) {
                return kotlinx.coroutines.flow.flow { emit(Resource.Error("Tipe file gambar tidak valid (JPG, JPEG, PNG).")) }
            }
        }
        return repository.updatePost(postId, title.trim(), content.trim(), photo)
    }
}