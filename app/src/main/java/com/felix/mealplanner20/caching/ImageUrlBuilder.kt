package com.felix.mealplanner20.caching

object ImageUrlBuilder {
    private const val EXPIRES_DEFAULT = 900L //15min
    fun recipe(baseUrl: String, key: String, verify: Boolean = false, expiresSeconds: Long = EXPIRES_DEFAULT): String {
        val safeKey = android.net.Uri.encode(key, "/")
        return "$baseUrl/images/recipe/$safeKey"
    }

    fun description(baseUrl: String, key: String, verify: Boolean = false, expiresSeconds: Long = EXPIRES_DEFAULT): String {
        val safeKey = android.net.Uri.encode(key, "/")
        return "$baseUrl/images/description/$safeKey"
    }

    fun profile(baseUrl: String, username: String, verify: Boolean = false, expiresSeconds: Long = EXPIRES_DEFAULT): String {
        val safeUser = android.net.Uri.encode(username, "")
        return "$$baseUrl/images/profile/$$safeUser?expiresSeconds=$$expiresSeconds$${if (verify) "&verify=1" else ""}"
    }

    // Optional: eigenes Profil (mit Auth-Header im Request, falls geschützt)
    fun ownProfile(baseUrl: String, verify: Boolean = false, expiresSeconds: Long = EXPIRES_DEFAULT): String {
        return "$$baseUrl/images/profile?expiresSeconds=$$expiresSeconds${if (verify) "&verify=1" else ""}"
    }
}