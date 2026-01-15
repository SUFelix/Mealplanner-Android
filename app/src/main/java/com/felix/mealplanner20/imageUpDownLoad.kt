package com.felix.mealplanner20

import android.content.Context
import android.net.Uri
import android.util.Log
import com.felix.mealplanner20.apiService.ImageApiService
import com.felix.mealplanner20.use_cases.IMAGE_METADATA_CODE
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class ImageUpDownLoad @Inject constructor(
    private val imageApiService: ImageApiService
){
    suspend fun uploadImage(byteArray: ByteArray,token:String,code:String,bucket: BUCKET): Result<String> {
        return try {
            val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", "upload.jpg", requestFile)

            val headers = mapOf(
                "Authorization" to "Bearer $token",
                IMAGE_METADATA_CODE to code
            )

            val response = when(bucket){
                BUCKET.RECIPE -> imageApiService.uploadRecipeImage(body,headers)
                BUCKET.PROFILE-> imageApiService.uploadProfileImage(body,headers)
                BUCKET.DESCRIPTION -> imageApiService.uploadDescriptionImage(body,headers)
            }
            val body1 = response.body()
            if (response.isSuccessful) {

                Result.success(body1?: "Erfolgreich hochgeladen!")

            } else {
                Result.failure(Exception("Fehler: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadOwnProfilePicture(header:Map<String, String>): ByteArray? {
            try {
                val response = imageApiService.fetchOwnProfileImageFromOnlineSource(headers = header)

                if (response.isSuccessful) {

                    val byteArray = response.body()?.bytes()

                    byteArray?.let {byteArray->
                        return byteArray
                    }
                }
                return null
            } catch (e: Exception) {
                Log.e("Failure when trying to fetch picture","$e")
            }
        return null
    }

    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

            inputStream?.use {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (it.read(buffer).also { len -> length = len } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                byteArrayOutputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

enum class BUCKET{
    PROFILE,
    RECIPE,
    DESCRIPTION
}