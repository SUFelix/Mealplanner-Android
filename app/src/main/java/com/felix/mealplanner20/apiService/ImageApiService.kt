package com.felix.mealplanner20.apiService

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ImageApiService {

    @GET("images/description/{key}")
    suspend fun fetchDescriptionImageFromOnlineSource(
        @Path(value = "key", encoded = true) key: String,
        @Query("expiresSeconds") expiresSeconds: Long = 900,
        @Query("verify") verify: Int = 0
    ): Response<ResponseBody>

    @GET("images/recipe/{key}")
    suspend fun fetchRecipeImageFromOnlineSource(
        @Path(value = "key", encoded = true) key: String,
        @Query("expiresSeconds") expiresSeconds: Long = 900,
        @Query("verify") verify: Int = 0
    ): Response<ResponseBody>


    @GET("images/profile/{username}")
    suspend fun fetchProfileImageFromOnlineSource(
        @Path("username") username: String,
        @Query("expiresSeconds") expiresSeconds: Long = 900,
        @Query("verify") verify: Int = 0
    ): Response<ResponseBody>

    @GET("images/profile")
    suspend fun fetchOwnProfileImageFromOnlineSource(
        @HeaderMap headers: Map<String, String>,
        @Query("expiresSeconds") expiresSeconds: Long = 900,
        @Query("verify") verify: Int = 0
    ): Response<ResponseBody>

    @GET("images/recipe/{uri}")
    suspend fun fetchRecipeImageFromOnlineSource(@Path("uri") uri: String): Response<ResponseBody>

    @GET("images/description/{uri}")
    suspend fun fetchDescriptionImageFromOnlineSource(@Path("uri") uri: String): Response<ResponseBody>

    @GET("images/profile/{username}")
    suspend fun fetchProfileImageFromOnlineSource(@Path("username") username: String): Response<ResponseBody>

    @GET("images/profile")
    suspend fun fetchOwnProfileImageFromOnlineSource(
        @HeaderMap headers: Map<String, String>
    ): Response<ResponseBody>

    @Multipart
    @POST("/images/recipe")
    suspend fun uploadRecipeImage(
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String>
    ): Response<String>

    @Multipart
    @POST("/images/profile")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String>
    ): Response<String>

    @Multipart
    @POST("/images/description")
    suspend fun uploadDescriptionImage(
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String>
    ): Response<String>
}