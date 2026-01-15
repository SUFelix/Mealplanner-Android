package com.felix.mealplanner20.Meals.Data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.felix.mealplanner20.BUCKET
import com.felix.mealplanner20.ImageUpDownLoad
import com.felix.mealplanner20.Meals.Data.helpers.uriToByteArray
import com.felix.mealplanner20.apiService.ImageUriRequest
import com.felix.mealplanner20.apiService.ProfileApiService
import com.felix.mealplanner20.use_cases.IMAGE_METADATA_CODE
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val upDownLoad: ImageUpDownLoad
) {
    suspend fun uploadNewDescription(newDescription: String,token:String) {
        try {
            val headers = mapOf("Authorization" to "Bearer $token")
            val response = profileApiService.postDescription(newDescription,headers)
        } catch (e: Exception) {
            Log.e("Fehler beim Hochladen:"," ${e.message}")
        }
    }


    suspend fun uploadProfileImageAndUpdateUri(context: Context,uri:Uri, token: String, code:String): Response<Unit>?{
        val result = uploadProfileImage(context,uri,token,code)
        result?.let {
            if(it.isSuccess){
                val headers = mapOf(
                    "Authorization" to "Bearer $token",
                    IMAGE_METADATA_CODE to code
                )
                val imgageUriRequest = ImageUriRequest(uri.toString())
                return profileApiService.postNewImageUri(imgageUriRequest,headers)
            }
        }
        return null
    }

    private suspend fun uploadProfileImage(context: Context,uri:Uri, token: String, code:String): Result<String>? {
        return try{
            uriToByteArray(context,uri)?.let {bytes->
                upDownLoad.uploadImage(bytes, token, code, BUCKET.PROFILE)
            }
        }catch (e:Exception){
            Log.e("ERROR",e.stackTraceToString())
            return null
        }
    }

    suspend fun getOwnProfileDescription(token: String):String?{
        try {
            val headers = mapOf("Authorization" to "Bearer $token")
            val response = profileApiService.getOwnDescription(headers)

            Log.i("DESCRIPTION",response.toString())

            return response
        } catch (e: Exception) {
            Log.e("Fehler beim Hochladen:"," ${e.message}")
            return null
        }
    }

    suspend fun getOwnEmail(token: String):String?{
        try {
            val headers = mapOf("Authorization" to "Bearer $token")
            val response = profileApiService.getOwnEmail(headers)
            val email = response?.email
            Log.i("EMAIL",response.toString())

            return email
        } catch (e: Exception) {
            Log.i("EMAIL","FAIL")
            Log.e("Fehler beim Hochladen:"," ${e.message}")
            return null
        }
    }
    suspend fun getOwnProfilePicture(token: String): ByteArray? {
        return try{
            val headers = mapOf("Authorization" to "Bearer $token")
            upDownLoad.downloadOwnProfilePicture(headers)
        }catch (e:Exception){
            Log.e("ERROR",e.stackTraceToString())
            return null
        }
    }
}