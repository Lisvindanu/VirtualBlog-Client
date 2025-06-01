package com.virtualsblog.project.util

object ImageUtil {
    
    /**
     * Convert relative image URL to full URL
     */
    fun getFullImageUrl(imageUrl: String?): String? {
        if (imageUrl.isNullOrEmpty()) return null
        
        return if (imageUrl.startsWith("http")) {
            imageUrl
        } else {
            "${Constants.IMAGE_BASE_URL}$imageUrl"
        }
    }
    
    /**
     * Get full profile image URL
     */
    fun getProfileImageUrl(imageName: String?): String? {
        if (imageName.isNullOrEmpty()) return null
        
        return if (imageName.startsWith("http")) {
            imageName
        } else {
            "${Constants.IMAGE_BASE_URL}${Constants.PROFILE_IMAGE_PATH}$imageName"
        }
    }
    
    /**
     * Get post image URL
     */
    fun getPostImageUrl(imageUrl: String?): String? {
        if (imageUrl.isNullOrEmpty()) return null
        
        return if (imageUrl.startsWith("http")) {
            imageUrl
        } else {
            "${Constants.IMAGE_BASE_URL}$imageUrl"
        }
    }
}
